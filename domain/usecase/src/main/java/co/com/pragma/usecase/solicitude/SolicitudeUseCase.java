package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.constants.LogMessages;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.solicitude.exceptions.SolicitudeException;
import co.com.pragma.model.solicitude.exceptions.SolicitudeNullException;
import co.com.pragma.model.solicitude.exceptions.StateNotFoundException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.usecase.solicitude.utils.SolicitudeUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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
        return verifyLoanType(solicitude)
                .flatMap(this::verifyState)
                .flatMap(solicitudeRepository::save);
    }

    private Mono<Solicitude> verifyLoanType(Solicitude solicitude) {
        return loanTypeRepository.findOne(solicitude.getLoanType())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new LoanTypeNotFoundException())))
                .flatMap(loanType -> Mono.just(solicitude.toBuilder().loanType(loanType).build()));
    }

    private Mono<Solicitude> verifyState(Solicitude solicitude) {
        return stateRepository.findOne(solicitude.getState())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new StateNotFoundException())))
                .flatMap(state -> Mono.just(solicitude.toBuilder().state(state).build()));
    }

    // END Private methods *************************************************************

}
