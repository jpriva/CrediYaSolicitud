package co.com.pragma.api.state;

import co.com.pragma.api.dto.solicitude.LoanTypeResponseDTO;
import co.com.pragma.api.dto.solicitude.StateResponseDTO;
import co.com.pragma.api.mapper.solicitude.StateMapper;
import co.com.pragma.usecase.state.StateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StateHandler {
    private final StateUseCase stateUseCase;
    private final StateMapper mapper;

    public Mono<ServerResponse> listenGETStateUseCase(ServerRequest serverRequest) {
        Flux<StateResponseDTO> response = stateUseCase.getAllStates()
                .map(mapper::toResponseDto);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response, LoanTypeResponseDTO.class);
    }

}
