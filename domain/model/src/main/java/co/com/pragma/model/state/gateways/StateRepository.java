package co.com.pragma.model.state.gateways;

import co.com.pragma.model.state.State;
import reactor.core.publisher.Mono;

public interface StateRepository {
    Mono<State> findOne(State state);
}
