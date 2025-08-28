package co.com.pragma.r2dbc;

import co.com.pragma.model.state.State;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.r2dbc.mapper.PersistenceStateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StateEntityRepositoryAdapter implements StateRepository {

    private final StateEntityRepository stateRepository;
    private final PersistenceStateMapper stateMapper;

    @Override
    public Mono<State> findOne(State state) {
        return stateRepository.findOne(Example.of(stateMapper.toEntity(state)))
                .map(stateMapper::toDomain);
    }
}
