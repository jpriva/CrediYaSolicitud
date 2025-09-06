package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudeReportUseCaseTest {

    @Mock
    private SolicitudeRepository repository;

    @Mock
    private UserPort userPort;

    @Mock
    private LoggerPort logger;

    @InjectMocks
    private SolicitudeReportUseCase useCase;

    private SolicitudeReport partialSolicitudeReport;
    private UserProjection userProjection1;

    @BeforeEach
    void setUp() {
        partialSolicitudeReport = SolicitudeReport.builder()
                .solicitudeId(1)
                .value(BigDecimal.valueOf(1000))
                .deadline(12)
                .clientEmail("test@test.com")
                .stateName("PENDIENTE")
                .loanTypeName("PERSONAL")
                .interestRate(BigDecimal.valueOf(1.5))
                .totalMonthlyPay(BigDecimal.valueOf(100))
                .build();

        userProjection1 = UserProjection.builder()
                .email("test@test.com")
                .name("Test User")
                .idNumber("123")
                .baseSalary(BigDecimal.valueOf(50000))
                .build();
    }

    @Nested
    class GetSolicitudeReportReactiveTests {

        @Test
        void withExistingUser_shouldBuildFullReport() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().build();
            when(repository.findSolicitudeReport(filter)).thenReturn(Flux.just(partialSolicitudeReport));
            when(userPort.getUserByEmail("test@test.com")).thenReturn(Mono.just(userProjection1));

            Flux<SolicitudeReport> result = useCase.getSolicitudeReportReactive(filter);

            StepVerifier.create(result)
                    .expectNextMatches(report ->
                            report.getClientName().equals("Test User") &&
                                    report.getSolicitudeId() == 1
                    )
                    .verifyComplete();
        }

        @Test
        void withNonExistingUser_shouldBuildPartialReport() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().build();
            when(repository.findSolicitudeReport(filter)).thenReturn(Flux.just(partialSolicitudeReport));
            when(userPort.getUserByEmail("test@test.com")).thenReturn(Mono.empty());

            Flux<SolicitudeReport> result = useCase.getSolicitudeReportReactive(filter);

            StepVerifier.create(result)
                    .expectNextMatches(report ->
                            report.getClientName() == null && // User data should be null
                                    report.getSolicitudeId() == 1
                    )
                    .verifyComplete();
        }
    }
}