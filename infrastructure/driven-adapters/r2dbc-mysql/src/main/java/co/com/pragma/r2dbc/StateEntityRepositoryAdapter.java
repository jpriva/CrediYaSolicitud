package co.com.pragma.r2dbc;

import co.com.pragma.model.state.State;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.r2dbc.mapper.PersistenceStateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @Override
    public Flux<State> findAll() {
        return stateRepository.findAll().map(stateMapper::toDomain);
    }

    @Override
    public Flux<State> saveAll(List<State> states) {
        return stateRepository.saveAll(states.stream().map(stateMapper::toEntity).toList())
                .map(stateMapper::toDomain);
    }

    @Override
    public Mono<State> findByName(String name) {
        return stateRepository.findByName(name).map(stateMapper::toDomain);
    }
}
