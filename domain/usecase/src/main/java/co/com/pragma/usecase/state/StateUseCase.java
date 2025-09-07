package co.com.pragma.usecase.state;

import co.com.pragma.model.state.State;
import co.com.pragma.model.state.gateways.StateRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class StateUseCase {

    private final StateRepository stateRepository;

    public Flux<State> getAllStates() {
        return stateRepository.findAll();
    }

    public Flux<State> saveAll(List<State> states) {
        return Flux.fromIterable(states)
                .filterWhen(state ->
                        stateRepository.findByName(state.getName())
                                .hasElement()
                                .map(exists -> !exists)
                )
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(stateRepository::saveAll);
    }
}
