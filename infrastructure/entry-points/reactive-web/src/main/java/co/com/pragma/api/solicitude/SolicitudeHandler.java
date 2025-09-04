package co.com.pragma.api.solicitude;

import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
import co.com.pragma.api.dto.solicitude.SolicitudeRequestDTO;
import co.com.pragma.api.mapper.report.SolicitudeReportMapper;
import co.com.pragma.api.mapper.report.FilterMapper;
import co.com.pragma.api.mapper.solicitude.SolicitudeMapper;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.usecase.solicitude.SolicitudeReportUseCase;
import co.com.pragma.usecase.solicitude.SolicitudeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SolicitudeHandler {
    private final SolicitudeUseCase solicitudeUseCase;
    private final SolicitudeReportUseCase solicitudeReportUseCase;
    private final LoggerPort logger;
    private final SolicitudeMapper solicitudeMapper;
    private final SolicitudeReportMapper solicitudeReportMapper;
    private final JwtProviderPort jwtProvider;


    public Mono<ServerResponse> listenPOSTSaveSolicitudeUseCase(ServerRequest serverRequest) {
        Mono<JwtData> tokenMono = Mono.justOrEmpty(serverRequest.headers().firstHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(bearer -> bearer.substring(7))
                .map(jwtProvider::getClaims)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()));

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

    public Mono<ServerResponse> listenGETSolicitudeReportUseCase(ServerRequest serverRequest) {
        SolicitudeReportFilter filter = FilterMapper.toFilter(serverRequest.queryParams());
        Flux<SolicitudeReportResponseDTO> reportFlux = Mono.just(filter)
                .flatMapMany(solicitudeReportUseCase::getSolicitudeReport)
                .map(solicitudeReportMapper::toResponseDto);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reportFlux, SolicitudeReportResponseDTO.class);
    }
}
