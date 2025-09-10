package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.exceptions.InvalidFieldException;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.SolicitudeNullException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.sqs.gateways.SQSPort;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.exceptions.StateNotFoundException;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.template.EmailTemplate;
import co.com.pragma.model.template.gateways.TemplatePort;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.usecase.solicitude.utils.NotificationUtils;
import co.com.pragma.usecase.solicitude.utils.SolicitudeUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class SolicitudeUseCase {

    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final SolicitudeRepository solicitudeRepository;
    private final LoggerPort logger;
    private final TransactionalPort transactionalPort;
    private final SQSPort sqsPort;
    private final TemplatePort templatePort;

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
                .doOnSuccess(savedSolicitude -> logger.info(LogMessages.SAVED_SOLICITUDE + " with ID: {}", savedSolicitude.getSolicitudeId()))
                .as(transactionalPort::transactional);
    }

    public Mono<Solicitude> approveRejectSolicitudeState(Integer solicitudeId, String state) {
        return SolicitudeUtils.validateApproveRejectRequestedData(solicitudeId, state)
                .then(Mono.defer(()->{
                    Mono<State> newState = stateRepository.findByName(state).switchIfEmpty(Mono.error(new StateNotFoundException()));
                    Mono<Solicitude> solicitude= solicitudeRepository.findById(solicitudeId).switchIfEmpty(Mono.error(new SolicitudeNullException()));
                    return Mono.zip(newState, solicitude);
                }))
                .flatMap(tStateSolicitude ->
                        saveStateChange(tStateSolicitude.getT1(), tStateSolicitude.getT2())
                ).doOnSuccess(solicitude -> {
                    Map<String,Object> context = NotificationUtils.solicitudeChangeStateBody(solicitude);
                    String emailBody = templatePort.process(EmailTemplate.STATE_CHANGE, context);
                    sqsPort.sendEmail(
                            solicitude.getEmail(),
                            NotificationUtils.DEFAULT_STATE_CHANGE,
                            emailBody
                    );
                }).as(transactionalPort::transactional);
    }

    // START Private methods ***********************************************************

    private Mono<Solicitude> saveStateChange(State state, Solicitude solicitude) {
        return completeSolicitude(solicitude)
                .flatMap(solicitudeFull -> {
                    if (state.getName().equals(solicitudeFull.getState().getName()))
                        return Mono.error(new InvalidFieldException(DefaultValues.STATE_FIELD));
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

    // END Private methods *************************************************************

}
