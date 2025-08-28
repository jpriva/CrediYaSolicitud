package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.SolicitudeRequestDTO;
import co.com.pragma.api.mapper.SolicitudeMapper;
import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.exceptions.SolicitudeException;
import co.com.pragma.usecase.solicitude.SolicitudeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class Handler {
    private final SolicitudeUseCase solicitudeUseCase;
    private final LoggerPort logger;
    private final SolicitudeMapper solicitudeMapper;
    public Mono<ServerResponse> listenPOSTSaveSolicitudeUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudeRequestDTO.class)
                .map(solicitudeMapper::toDomain)
                .flatMap(solicitudeUseCase::saveSolicitude)
                .map(solicitudeMapper::toResponseDto)
                .flatMap( savedSolicitude ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedSolicitude)
                ).doOnError(ex -> logger.error(ex.getMessage(), ex))
                .onErrorResume(ServerWebInputException.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ErrorDTO.builder()
                                        .timestamp(Instant.now())
                                        .path(serverRequest.path())
                                        .code(Errors.FAIL_READ_REQUEST_CODE)
                                        .message(Errors.FAIL_READ_REQUEST)
                                        .build())
                ).onErrorResume(SolicitudeException.class, ex ->
                        ServerResponse.status(HttpStatus.valueOf(ex.getWebStatus()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ErrorDTO.builder()
                                        .timestamp(Instant.now())
                                        .path(serverRequest.path())
                                        .code(ex.getCode())
                                        .message(ex.getMessage())
                                        .build())
                ).onErrorResume(Exception.class, ex ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ErrorDTO.builder()
                                        .timestamp(Instant.now())
                                        .path(serverRequest.path())
                                        .code(Errors.UNKNOWN_CODE)
                                        .message(Errors.UNKNOWN_ERROR)
                                        .build())
                );
    }
}
