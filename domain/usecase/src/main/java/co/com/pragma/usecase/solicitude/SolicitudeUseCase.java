package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.exceptions.InvalidFieldException;
import co.com.pragma.model.exceptions.UserNotFoundException;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.SolicitudeNullException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.sqs.Metric;
import co.com.pragma.model.sqs.Metrics;
import co.com.pragma.model.sqs.gateways.SQSPort;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.exceptions.StateNotFoundException;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.template.EmailMessage;
import co.com.pragma.model.template.EmailTemplate;
import co.com.pragma.model.template.gateways.TemplatePort;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import co.com.pragma.usecase.solicitude.utils.NotificationUtils;
import co.com.pragma.usecase.solicitude.utils.SolicitudeUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SolicitudeUseCase {

    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final SolicitudeRepository solicitudeRepository;
    private final LoggerPort logger;
    private final TransactionalPort transactionalPort;
    private final SQSPort sqsPort;
    private final TemplatePort templatePort;
    private final UserPort userPort;
    private final JwtProviderPort jwtPort;

    public Mono<Solicitude> saveSolicitude(Solicitude solicitude, String idNumber, JwtData token) {
        return Mono.justOrEmpty(solicitude)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new SolicitudeNullException())))
                .flatMap(SolicitudeUtils::trim)
                .flatMap(s -> SolicitudeUtils.validateFromToken(s, idNumber, token))
                .doOnNext(solicitudeMono -> logger.info("SolicitudeWithEmail [email: {}, value: {}, deadline: {}]", solicitudeMono.getEmail(), solicitudeMono.getValue(), solicitudeMono.getDeadline()))
                .flatMap(SolicitudeUtils::validateFields)
                .map(solicitudeMono -> solicitudeMono.toBuilder().state(State.builder().name(DefaultValues.PENDING_STATE).build()).build())
                .flatMap(this::saveNewSolicitudeTransaction)
                .doFirst(() -> logger.info(LogMessages.START_SAVING_SOLICITUDE_PROCESS + " for user with idNumber: {}", idNumber))
                .doOnError(ex -> logger.error(Errors.ERROR_SAVING_SOLICITUDE + " for user with idNumber: {}", idNumber, ex))
                .flatMap(savedSolicitude ->
                        postProcessSaveSolicitude(savedSolicitude)
                                .onErrorResume(ex -> {
                                    logger.error("Error calculating debt capacity for loan application with Id: {}", savedSolicitude.getSolicitudeId(), ex);
                                    return Mono.empty();
                                })
                                .thenReturn(savedSolicitude)
                ).doOnSuccess(s -> logger.info("Successfully saved solicitude with ID: {}", s.getSolicitudeId()))
                .as(transactionalPort::transactional);
    }

    public Mono<Solicitude> approveRejectSolicitudeState(Integer solicitudeId, String state) {
        return changeState(solicitudeId, state, false)
                .flatMap(solicitude ->
                        stateNotification(solicitude).flatMap(sqsPort::sendEmail)
                                .onErrorResume(error -> {
                                    logger.error("Failed to send state change notification for solicitude {}. Error will be ignored.", solicitude.getSolicitudeId(), error);
                                    return Mono.empty();
                                })
                                .thenReturn(solicitude)
                )
                .doFirst(() -> logger.info("Starting state change process for solicitude ID: {} to state: {}", solicitudeId, state))
                .doOnSuccess(solicitude -> logger.info("Successfully changed state for solicitude ID: {} to: {}", solicitude.getSolicitudeId(), solicitude.getState().getName()))
                .doOnError(ex -> logger.error("Error changing state for solicitude ID: {}", solicitudeId, ex))
                .as(transactionalPort::transactional);
    }

    public Mono<EmailMessage> debtCapacityStateChange(Integer solicitudeId, String state) {
        return changeState(solicitudeId, state, true)
                .flatMap(solicitude -> payPlanNotification(solicitude, state))
                .doFirst(() -> logger.info("Starting debt capacity state change for solicitude ID: {} to state: {}", solicitudeId, state))
                .doOnSuccess(email -> logger.info("Successfully generated notification for solicitude ID: {}", solicitudeId))
                .doOnError(ex -> logger.error("Error during debt capacity state change for solicitude ID: {}", solicitudeId, ex))
                .as(transactionalPort::transactional);
    }

    // START Private methods ***********************************************************

    private Mono<EmailMessage> stateNotification(Solicitude solicitude) {
        return Mono.fromCallable(() -> NotificationUtils.solicitudeChangeStateBody(solicitude))
                .flatMap(context -> templatePort.process(EmailTemplate.STATE_CHANGE.getTemplateName(), context))
                .doOnSuccess(body -> logger.debug("Generated State Notification Email Body:\n{}", body))
                .map(body -> EmailMessage.builder().to(solicitude.getEmail()).subject(NotificationUtils.DEFAULT_STATE_CHANGE).body(body).build());
    }

    private Mono<EmailMessage> payPlanNotification(Solicitude solicitude, String state) {
        if (state.equals(DefaultValues.APPROVED_STATE)) {
            logger.info("Generating pay plan notification for approved solicitude ID: {}", solicitude.getSolicitudeId());
            return Mono.fromCallable(() -> NotificationUtils.userPayPlan(solicitude))
                    .flatMap(context -> templatePort.process(EmailTemplate.PAY_PLAN.getTemplateName(), context))
                    .doOnSuccess(body -> logger.info("Generated Pay Plan Email Body:\n{}", body))
                    .map(body -> EmailMessage.builder().to(solicitude.getEmail()).subject(NotificationUtils.DEFAULT_PAY_PLAN).body(body).build());
        }
        logger.info("Generating standard state change notification for solicitude ID: {}", solicitude.getSolicitudeId());
        return stateNotification(solicitude);
    }

    private Mono<Solicitude> changeState(Integer solicitudeId, String state, boolean acceptsManual) {
        return SolicitudeUtils.validateApproveRejectRequestedData(solicitudeId, state, acceptsManual)
                .then(Mono.defer(() -> {
                    Mono<State> newState = stateRepository.findByName(state).switchIfEmpty(Mono.error(new StateNotFoundException()));
                    Mono<Solicitude> solicitude = solicitudeRepository.findById(solicitudeId).switchIfEmpty(Mono.error(new SolicitudeNullException()));
                    return Mono.zip(newState, solicitude);
                }))
                .flatMap(tStateSolicitude ->
                        saveStateChange(tStateSolicitude.getT1(), tStateSolicitude.getT2())
                                .flatMap(this::sendApprovedMetric)
                );
    }

    private Mono<Solicitude> sendApprovedMetric(Solicitude solicitude) {
        if (solicitude == null || solicitude.getState() == null || solicitude.getState().getName() == null) return Mono.empty();
        if (!solicitude.getState().getName().equals(DefaultValues.APPROVED_STATE)) return Mono.empty();
        logger.info("Sending approved metric for solicitude ID: {}", solicitude.getSolicitudeId());
        Metric metric = new Metric(Metrics.QUANTITY_METRIC, BigDecimal.ONE);
        return sqsPort.sendMetric(metric)
                .thenReturn(solicitude);
    }

    private Mono<Solicitude> saveStateChange(State state, Solicitude solicitude) {
        return completeSolicitude(solicitude)
                .flatMap(solicitudeFull -> {
                    if (state.getName().equals(solicitudeFull.getState().getName()))
                        return Mono.error(new InvalidFieldException(DefaultValues.STATE_FIELD));
                    logger.info("Saving state change for solicitude ID: {} to state: {}", solicitudeFull.getSolicitudeId(), state.getName());
                    return solicitudeRepository.save(solicitudeFull.toBuilder().state(state).build())
                            .map(s ->
                                    s.toBuilder().state(state).loanType(solicitudeFull.getLoanType()).build()
                            );
                });
    }

    private Mono<Solicitude> saveNewSolicitudeTransaction(Solicitude solicitude) {
        return completeSolicitude(solicitude)
                .flatMap(solicitudeFull ->
                        SolicitudeUtils.verifySolicitudeLoanType(solicitudeFull)
                                .flatMap(solicitudeRepository::save)
                                .map(savedSolicitude -> savedSolicitude.toBuilder()
                                        .loanType(solicitudeFull.getLoanType())
                                        .state(solicitudeFull.getState())
                                        .build())
                );
    }

    private Mono<Solicitude> completeSolicitude(Solicitude solicitude) {
        Mono<LoanType> loanTypeDb = loanTypeRepository.findById(solicitude.getLoanType().getLoanTypeId())
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException()));

        Mono<State> stateDb = stateRepository.findOne(solicitude.getState())
                .switchIfEmpty(Mono.error(new StateNotFoundException()));
        return Mono.zip(loanTypeDb, stateDb).map(tuple ->
                solicitude.toBuilder()
                        .loanType(tuple.getT1())
                        .state(tuple.getT2())
                        .build()
        );
    }

    private Mono<Void> postProcessSaveSolicitude(Solicitude savedSolicitude) {
        if (savedSolicitude == null || savedSolicitude.getLoanType() == null || savedSolicitude.getEmail() == null) {
            return Mono.empty();
        }
        boolean autoValidation = savedSolicitude.getLoanType().getAutoValidation();
        if (!autoValidation) {
            return Mono.empty();
        }
        logger.info(LogMessages.SAVED_SOLICITUDE + " with ID: {}", savedSolicitude.getSolicitudeId());
        String email = savedSolicitude.getEmail();
        return findUserByEmail(email)
                .flatMap(user -> solicitudeRepository.findTotalMonthlyFee(email)
                        .map(fee ->
                                SolicitudeUtils.buildDebtCapacity(user, savedSolicitude, fee, jwtPort)
                        )
                ).doOnNext(debtCapacity -> logger.info("Sending debt capacity calculation to SQS for solicitude ID: {}", savedSolicitude.getSolicitudeId()))
                .flatMap(sqsPort::sendDebtCapacity);
    }

    private Mono<UserProjection> findUserByEmail(String email) {
        return userPort.getUserByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException(email)));
    }

    // END Private methods *************************************************************

}
