package co.com.pragma.api;

import co.com.pragma.api.constants.Constants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.SolicitudeRequestDTO;
import co.com.pragma.api.dto.SolicitudeResponseDTO;
import co.com.pragma.api.exception.handler.GlobalExceptionHandler;
import co.com.pragma.api.mapper.SolicitudeMapper;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.CustomException;
import co.com.pragma.usecase.solicitude.SolicitudeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SolicitudeUseCase solicitudeUseCase;

    @MockitoBean
    private LoggerPort logger;

    @MockitoBean
    private SolicitudeMapper solicitudeMapper;

    @Test
    void shouldRouteToHandlerAndReturnCreatedOnSuccess() {
        SolicitudeRequestDTO requestDTO = SolicitudeRequestDTO.builder()
                .idNumber("12345")
                .value(BigDecimal.TEN)
                .loanTypeId(1)
                .build();
        SolicitudeResponseDTO responseDTO = SolicitudeResponseDTO.builder().solicitudeId(1).build();
        Solicitude domainObject = Solicitude.builder().build();
        Solicitude savedDomainObject = Solicitude.builder().solicitudeId(1).build();

        when(solicitudeMapper.toDomain(any(SolicitudeRequestDTO.class))).thenReturn(domainObject);
        when(solicitudeUseCase.saveSolicitude(any(Solicitude.class), any(String.class))).thenReturn(Mono.just(savedDomainObject));
        when(solicitudeMapper.toResponseDto(any(Solicitude.class))).thenReturn(responseDTO);

        webTestClient.post()
                .uri(Constants.API_SOLICITUDE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SolicitudeResponseDTO.class)
                .isEqualTo(responseDTO);
    }

    @Test
    void shouldRouteToHandlerAndReturnBadRequestOnError() {
        SolicitudeRequestDTO requestDTO = SolicitudeRequestDTO.builder()
                .idNumber("12345")
                .value(BigDecimal.TEN)
                .loanTypeId(1)
                .build();
        Solicitude domainObject = Solicitude.builder().build();
        CustomException customException = new CustomException("Invalid input", "VAL-001", 400);

        when(solicitudeMapper.toDomain(any(SolicitudeRequestDTO.class))).thenReturn(domainObject);
        when(solicitudeUseCase.saveSolicitude(any(Solicitude.class), any(String.class))).thenReturn(Mono.error(customException));

        webTestClient.post()
                .uri(Constants.API_SOLICITUDE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDTO.class)
                .value(response -> assertEquals("VAL-001", response.getCode()));
    }
}