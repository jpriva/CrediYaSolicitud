package co.com.pragma.r2dbc;

import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.solicitude.exceptions.StateNotFoundException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.r2dbc.mapper.PersistenceLoanTypeMapper;
import co.com.pragma.r2dbc.mapper.PersistenceSolicitudeMapper;
import co.com.pragma.r2dbc.mapper.PersistenceStateMapper;
import co.com.pragma.r2dbc.util.SolicitudeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class SolicitudeEntityRepositoryAdapter implements SolicitudeRepository {

    private final SolicitudeEntityRepository solicitudeRepository;
    private final PersistenceSolicitudeMapper solicitudeMapper;
    private final LoanTypeEntityRepository loanTypeRepository;
    private final PersistenceLoanTypeMapper loanTypeMapper;
    private final StateEntityRepository stateRepository;
    private final PersistenceStateMapper stateMapper;



    @Override
    public Mono<Solicitude> save(Solicitude solicitude) {
        return loanTypeRepository.findById(solicitude.getLoanType().getLoanTypeId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new LoanTypeNotFoundException())))
                .flatMap(loanTypeEntity ->
                        stateRepository.findById(solicitude.getState().getStateId())
                                .switchIfEmpty(Mono.defer(() -> Mono.error(new StateNotFoundException())))
                                .flatMap(stateEntity ->
                                        solicitudeRepository.save(solicitudeMapper.toEntity(solicitude))
                                                .map( solicitudeEntity ->
                                                        SolicitudeUtil.setSolicitudeLoanTypeAndState(
                                                                solicitudeMapper.toDomain(solicitudeEntity),
                                                                loanTypeMapper.toDomain(loanTypeEntity),
                                                                stateMapper.toDomain(stateEntity)
                                                        )
                                                )
                                )
                );
    }
}
