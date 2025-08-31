package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudeRequestDTO;
import co.com.pragma.api.mapper.SolicitudeMapper;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.usecase.solicitude.SolicitudeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final SolicitudeUseCase solicitudeUseCase;
    private final LoggerPort logger;
    private final SolicitudeMapper solicitudeMapper;

    public Mono<ServerResponse> listenPOSTSaveSolicitudeUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudeRequestDTO.class)
                .flatMap(request ->
                        solicitudeUseCase.saveSolicitude(
                                solicitudeMapper.toDomain(request),
                                request.getIdNumber()
                        )
                )
                .map(solicitudeMapper::toResponseDto)
                .flatMap(savedSolicitude ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedSolicitude)
                );
    }
}
