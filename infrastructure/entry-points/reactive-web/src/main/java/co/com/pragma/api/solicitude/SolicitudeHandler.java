package co.com.pragma.api.solicitude;

import co.com.pragma.api.dto.solicitude.DebtCapacityRequestDTO;
import co.com.pragma.api.dto.solicitude.SolicitudeRequestDTO;
import co.com.pragma.api.dto.solicitude.UpdateStatusRequestDTO;
import co.com.pragma.api.mapper.email.EmailDTOMapper;
import co.com.pragma.api.mapper.page.PageMapper;
import co.com.pragma.api.mapper.report.FilterMapper;
import co.com.pragma.api.mapper.solicitude.SolicitudeMapper;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.usecase.solicitude.SolicitudeReportUseCase;
import co.com.pragma.usecase.solicitude.SolicitudeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SolicitudeHandler {
    private final SolicitudeUseCase solicitudeUseCase;
    private final SolicitudeReportUseCase solicitudeReportUseCase;
    private final SolicitudeMapper solicitudeMapper;
    private final PageMapper pageMapper;
    private final EmailDTOMapper emailMapper;


    public Mono<ServerResponse> listenPOSTSaveSolicitudeUseCase(ServerRequest serverRequest) {
        Mono<JwtData> jwtDataMono = serverRequest.principal()
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(jwt -> {
                    Integer roleId = Optional.ofNullable(jwt.getClaim("roleId"))
                            .map(val -> {
                                if (val instanceof Integer i) return i;
                                if (val instanceof Number n) return n.intValue();
                                if (val instanceof String s) return Integer.valueOf(s.trim());
                                return null;
                            })
                            .orElse(null);
                    return new JwtData(
                            jwt.getSubject(),
                            jwt.getClaimAsString("role"),
                            roleId,
                            jwt.getClaimAsString("name"),
                            jwt.getClaimAsString("idNumber"));
                });

        Mono<SolicitudeRequestDTO> solicitudeMono = serverRequest.bodyToMono(SolicitudeRequestDTO.class);

        return Mono.zip(solicitudeMono, jwtDataMono)
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
        return Mono.just(filter)
                .flatMap(solicitudeReportUseCase::getSolicitudeReport)
                .map(pageMapper::toDto)
                .flatMap(responsePage ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(responsePage)
                );
    }

    public Mono<ServerResponse> listenPUTUpdateSolicitudeStatusUseCase(ServerRequest serverRequest) {
        Integer solicitudeId = Integer.valueOf(serverRequest.pathVariable("id"));
        return serverRequest.bodyToMono(UpdateStatusRequestDTO.class)
                .flatMap(updateRequest -> {
                    String state = updateRequest.getState();
                    return solicitudeUseCase.approveRejectSolicitudeState(
                            solicitudeId,
                            state
                    );
                })
                .map(solicitudeMapper::toResponseDto)
                .flatMap(updatedSolicitude ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updatedSolicitude)
                );
    }

    public Mono<ServerResponse> listenPOSTDebtCapacityUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(DebtCapacityRequestDTO.class)
                .flatMap(debtCapacityRequest ->
                        solicitudeUseCase.debtCapacityStateChange(
                                debtCapacityRequest.getSolicitudeId(),
                                debtCapacityRequest.getState()
                        ).map(emailMapper::toDto)
                ).flatMap(email ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(email)
                );
    }
}
