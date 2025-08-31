package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.SolicitudeNullException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.exceptions.StateNotFoundException;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.gateways.UserPort;
import co.com.pragma.usecase.solicitude.utils.SolicitudeUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudeUseCase {

    // START Injected Properties ****************************************************************

    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final SolicitudeRepository solicitudeRepository;
    private final UserPort userPort;
    private final LoggerPort logger;
    private final TransactionalPort transactionalPort;

    // END Injected Properties ******************************************************************

    public Mono<Solicitude> saveSolicitude(Solicitude solicitude, String idNumber) {
        return Mono.justOrEmpty(solicitude)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new SolicitudeNullException())))
                .flatMap(SolicitudeUtils::trim)
                .flatMap(solicitudeMono -> getSolicitudeEmailByIdNumber(solicitudeMono, idNumber))
                .doOnNext(solicitudeMono -> logger.info("SolicitudeWithEmail [email: {}, value: {}, deadline: {}]",solicitudeMono.getEmail(), solicitudeMono.getValue(),solicitudeMono.getDeadline()))
                .flatMap(SolicitudeUtils::validateFields)
                .map(solicitudeMono -> solicitudeMono.toBuilder().state(State.builder().name(DefaultValues.PENDING_STATE).build()).build())
                .flatMap(this::saveSolicitudeTransaction)
                .doFirst(() -> logger.info(LogMessages.START_SAVING_SOLICITUDE_PROCESS + " for user with idNumber: {}", idNumber))
                .doOnError(ex -> logger.error(Errors.ERROR_SAVING_SOLICITUDE + " for user with idNumber: {}", idNumber, ex))
                .doOnSuccess(savedSolicitude -> logger.info(LogMessages.SAVED_SOLICITUDE + " with ID: {}", savedSolicitude.getSolicitudeId()))
                .as(transactionalPort::transactional);
    }

    // START Private methods ***********************************************************

    private Mono<Solicitude> getSolicitudeEmailByIdNumber(Solicitude solicitude, String idNumber) {
        return userPort.getUserByIdNumber(idNumber)
                .map(user -> solicitude.toBuilder().email(user.getEmail()).build());
    }

    private Mono<Solicitude> saveSolicitudeTransaction(Solicitude solicitude) {
        Mono<LoanType> loanTypeMono = loanTypeRepository.findById(solicitude.getLoanType().getLoanTypeId())
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException()));

        Mono<State> stateMono = stateRepository.findOne(solicitude.getState())
                .switchIfEmpty(Mono.error(new StateNotFoundException()));

        return Mono.zip(loanTypeMono, stateMono)
                .flatMap(tuple ->
                    SolicitudeUtils.verifySolicitudeLoanType(solicitude, tuple.getT1())
                            .map(validatedSolicitude -> validatedSolicitude.toBuilder()
                                    .loanType(tuple.getT1())
                                    .state(tuple.getT2())
                                    .build())
                            .flatMap(solicitudeRepository::save)
                            .map(savedSolicitude -> savedSolicitude.toBuilder()
                                    .loanType(tuple.getT1())
                                    .state(tuple.getT2())
                                    .build())
                );
    }

    // END Private methods *************************************************************

}
