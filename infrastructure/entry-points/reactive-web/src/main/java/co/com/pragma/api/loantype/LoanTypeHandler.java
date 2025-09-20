package co.com.pragma.api.loantype;

import co.com.pragma.api.dto.solicitude.LoanTypeResponseDTO;
import co.com.pragma.api.mapper.solicitude.LoanTypeMapper;
import co.com.pragma.usecase.loantype.LoanTypeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanTypeHandler {
    private final LoanTypeUseCase loanTypeUseCase;
    private final LoanTypeMapper mapper;

    public Mono<ServerResponse> listenGETLoanTypeUseCase(ServerRequest serverRequest) {
        Flux<LoanTypeResponseDTO> response = loanTypeUseCase.getAllLoanTypes()
                .map(mapper::toResponseDto);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response, LoanTypeResponseDTO.class);
    }
}
