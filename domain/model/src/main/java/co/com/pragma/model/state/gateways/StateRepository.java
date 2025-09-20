package co.com.pragma.model.state.gateways;

import co.com.pragma.model.state.State;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StateRepository {
    Mono<State> findOne(State state);
    Flux<State> findAll();
    Flux<State> saveAll(List<State> states);
    Mono<State> findByName(String name);
}
