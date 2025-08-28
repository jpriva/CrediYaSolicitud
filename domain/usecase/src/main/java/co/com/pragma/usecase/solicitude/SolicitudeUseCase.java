package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.*;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.usecase.solicitude.utils.SolicitudeUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.model.constants.DefaultValues.VALUE_FIELD;

@RequiredArgsConstructor
public class SolicitudeUseCase {

    // START Injected Properties ****************************************************************

    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final SolicitudeRepository solicitudeRepository;
    private final LoggerPort logger;
    private final TransactionalPort transactionalPort;

    // END Injected Properties ******************************************************************

    public Mono<Solicitude> saveSolicitude(Solicitude solicitude) {
        if (solicitude == null) {
            return Mono.error(new SolicitudeNullException());
        }
        solicitude.trim();
        SolicitudeException exception = SolicitudeUtils.validateFields(solicitude);
        if (exception != null) {
            return Mono.error(exception);
        }
        solicitude.setState(State.builder().name(DefaultValues.PENDING_STATE).build());
        return saveSolicitudeTransaction(solicitude)
                .doFirst(() -> logger.info(LogMessages.START_SAVING_SOLICITUDE_PROCESS + " for email: {}", solicitude.getEmail()))
                .doOnError(ex -> logger.error(Errors.ERROR_SAVING_SOLICITUDE + " for email: {}", solicitude.getEmail(), ex))
                .doOnSuccess(savedSolicitude -> logger.info(LogMessages.SAVED_SOLICITUDE + " with ID: {}", savedSolicitude.getSolicitudeId()))
                .as(transactionalPort::transactional);
    }

    // START Private methods ***********************************************************

    private Mono<Solicitude> saveSolicitudeTransaction(Solicitude solicitude) {
        Mono<LoanType> loanTypeMono = loanTypeRepository.findById(solicitude.getLoanType().getLoanTypeId())
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException()));
        Mono<State> stateMono = stateRepository.findOne(solicitude.getState())
                .switchIfEmpty(Mono.error(new StateNotFoundException()));
        return Mono.zip(loanTypeMono, stateMono)
                .flatMap(tuple -> {
                    LoanType loanType = tuple.getT1();
                    State state = tuple.getT2();
                    SolicitudeException exception =SolicitudeUtils.verifySolicitude(solicitude, loanType);
                    if (exception != null) {
                        return Mono.error(exception);
                    }
                    Solicitude validatedSolicitude = solicitude.toBuilder().loanType(loanType).state(state).build();
                    return solicitudeRepository.save(validatedSolicitude).map( savedSolicitude ->
                            solicitude.toBuilder().loanType(loanType).state(state).build()
                    );
                });

    }

    // END Private methods *************************************************************

}
