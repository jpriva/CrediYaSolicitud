package co.com.pragma.api;

import co.com.pragma.api.dto.SolicitudeRequestDTO;
import co.com.pragma.api.mapper.SolicitudeMapper;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
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
    private final JwtProviderPort jwtProvider;


    public Mono<ServerResponse> listenPOSTSaveSolicitudeUseCase(ServerRequest serverRequest) {
        Mono<JwtData> tokenMono = Mono.justOrEmpty(serverRequest.headers().firstHeader("Authorization"))
                .map(bearer -> bearer.substring(7))
                .map(jwtProvider::getClaims);
        Mono<SolicitudeRequestDTO> solicitudeMono = serverRequest.bodyToMono(SolicitudeRequestDTO.class);
        return Mono.zip(solicitudeMono, tokenMono)
                .flatMap(tuple ->
                        solicitudeUseCase.saveSolicitude(
                                solicitudeMapper.toDomain(tuple.getT1()), tuple.getT1().getIdNumber(), tuple.getT2())
                )
                .map(solicitudeMapper::toResponseDto)
                .flatMap(savedSolicitude ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedSolicitude)
                );
    }
}
