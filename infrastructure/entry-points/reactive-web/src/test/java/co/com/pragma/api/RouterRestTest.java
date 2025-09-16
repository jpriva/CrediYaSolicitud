package co.com.pragma.api;

import co.com.pragma.api.config.WebSecurityConfig;
import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.page.PaginatedResponseDTO;
import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
import co.com.pragma.api.dto.solicitude.SolicitudeRequestDTO;
import co.com.pragma.api.dto.solicitude.SolicitudeResponseDTO;
import co.com.pragma.api.exception.handler.CustomAccessDeniedHandler;
import co.com.pragma.api.exception.handler.CustomAuthenticationEntryPoint;
import co.com.pragma.api.exception.handler.GlobalExceptionHandler;
import co.com.pragma.api.mapper.email.EmailDTOMapper;
import co.com.pragma.api.mapper.page.PageMapper;
import co.com.pragma.api.mapper.report.SolicitudeReportMapper;
import co.com.pragma.api.mapper.solicitude.SolicitudeMapper;
import co.com.pragma.api.solicitude.SolicitudeHandler;
import co.com.pragma.api.solicitude.SolicitudeRouterRest;
import co.com.pragma.api.solicitude.documentation.ReportsDocumentation;
import co.com.pragma.model.exceptions.CustomException;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.page.PaginatedData;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.usecase.solicitude.SolicitudeReportUseCase;
import co.com.pragma.usecase.solicitude.SolicitudeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// We need to include the documentation class because it defines the router bean for the report endpoint.
@ContextConfiguration(classes = {
        SolicitudeRouterRest.class,
        ReportsDocumentation.class, // This class provides the GET /reporte route
        SolicitudeHandler.class,
        GlobalExceptionHandler.class, WebSecurityConfig.class,
        CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class
})
@WebFluxTest(controllers = GlobalExceptionHandler.class)
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SolicitudeUseCase solicitudeUseCase;

    @MockitoBean
    private LoggerPort logger;

    @MockitoBean
    private SolicitudeMapper solicitudeMapper;

    @MockitoBean
    private JwtProviderPort jwtProvider;

    @MockitoBean
    private SolicitudeReportUseCase solicitudeReportUseCase;

    @MockitoBean
    private SolicitudeReportMapper solicitudeReportMapper;

    @MockitoBean
    private PageMapper pageMapper;

    @MockitoBean
    private EmailDTOMapper emailMapper;

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void shouldRouteToHandlerAndReturnCreatedOnSuccess() {
        SolicitudeRequestDTO requestDTO = SolicitudeRequestDTO.builder()
                .idNumber("12345")
                .value(BigDecimal.TEN)
                .loanTypeId(1)
                .build();
        SolicitudeResponseDTO responseDTO = SolicitudeResponseDTO.builder().solicitudeId(1).build();
        Solicitude domainObject = Solicitude.builder().build();
        Solicitude savedDomainObject = Solicitude.builder().solicitudeId(1).build();
        JwtData jwtData = new JwtData("test@example.com", "CLIENTE", 1, "Test", "12345");

        when(jwtProvider.getClaims(anyString())).thenReturn(jwtData);
        when(solicitudeMapper.toDomain(any(SolicitudeRequestDTO.class))).thenReturn(domainObject);
        when(solicitudeUseCase.saveSolicitude(any(Solicitude.class), anyString(), any(JwtData.class))).thenReturn(Mono.just(savedDomainObject));
        when(solicitudeMapper.toResponseDto(any(Solicitude.class))).thenReturn(responseDTO);

        webTestClient.post()
                .uri(ApiConstants.ApiPath.SOLICITUDE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer dummy-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SolicitudeResponseDTO.class)
                .isEqualTo(responseDTO);
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void shouldRouteToHandlerAndReturnBadRequestOnError() {
        SolicitudeRequestDTO requestDTO = SolicitudeRequestDTO.builder()
                .idNumber("12345")
                .value(BigDecimal.TEN)
                .loanTypeId(1)
                .build();
        Solicitude domainObject = Solicitude.builder().build();
        CustomException customException = new CustomException("Invalid input", "VAL-001", 400);
        JwtData jwtData = new JwtData("test@example.com", "CLIENTE", 1, "Test", "12345");

        when(jwtProvider.getClaims(anyString())).thenReturn(jwtData);
        when(solicitudeMapper.toDomain(any(SolicitudeRequestDTO.class))).thenReturn(domainObject);
        when(solicitudeUseCase.saveSolicitude(any(Solicitude.class), anyString(), any(JwtData.class))).thenReturn(Mono.error(customException));

        webTestClient.post()
                .uri(ApiConstants.ApiPath.SOLICITUDE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer dummy-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDTO.class)
                .value(response -> assertEquals("VAL-001", response.getCode()));
    }

    @Test
    @WithMockUser(authorities = "ASESOR")
    void shouldRouteToReportHandlerAndReturnOk() {
        PaginatedData<SolicitudeReport> domainData = PaginatedData.<SolicitudeReport>builder()
                .content(Collections.singletonList(new SolicitudeReport()))
                .totalElements(1L)
                .build();

        PaginatedResponseDTO<SolicitudeReportResponseDTO> responseDTO = PaginatedResponseDTO.<SolicitudeReportResponseDTO>builder()
                .content(Collections.singletonList(new SolicitudeReportResponseDTO()))
                .totalElements(1L)
                .build();

        when(solicitudeReportUseCase.getSolicitudeReport(any(SolicitudeReportFilter.class))).thenReturn(Mono.just(domainData));
        when(pageMapper.toDto(any(PaginatedData.class))).thenReturn(responseDTO);

        webTestClient.get()
                .uri(ApiConstants.ApiPath.SOLICITUDE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedResponseDTO.class)
                .value(response -> {
                    assertEquals(1L, response.getTotalElements());
                    assertEquals(1, response.getContent().size());
                });
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
        // A user with the wrong role
    void shouldReturnForbiddenForReportWhenRoleIsIncorrect() {
        webTestClient.get()
                .uri(ApiConstants.ApiPath.SOLICITUDE_PATH)
                .exchange()
                .expectStatus().isForbidden();
    }
}