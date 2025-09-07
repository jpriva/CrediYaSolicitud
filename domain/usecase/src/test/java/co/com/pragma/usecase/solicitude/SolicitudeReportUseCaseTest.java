package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.page.PageableData;
import co.com.pragma.model.page.PaginatedData;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Nested
    class GetPaginatedSolicitudeReportTests {

        private SolicitudeReportFilter filterWithClientData;
        private SolicitudeReportFilter filterWithoutClientData;

        @BeforeEach
        void setUp() {
            PageableData pageable = PageableData.builder().page(0).size(10).build();
            filterWithClientData = SolicitudeReportFilter.builder()
                    .clientName("Test User")
                    .pageable(pageable)
                    .build();

            filterWithoutClientData = SolicitudeReportFilter.builder()
                    .stateName("PENDIENTE")
                    .pageable(pageable)
                    .build();
        }

        @Test
        void withClientFilters_shouldFetchUsersFirstAndReturnPaginatedData() {
            when(userPort.getUserByFilter(filterWithClientData)).thenReturn(Flux.just(userProjection1));
            when(repository.findSolicitudeReport(any(SolicitudeReportFilter.class))).thenReturn(Flux.just(partialSolicitudeReport));
            when(repository.countSolicitudeReport(any(SolicitudeReportFilter.class))).thenReturn(Mono.just(1L));

            Mono<PaginatedData<SolicitudeReport>> result = useCase.getSolicitudeReport(filterWithClientData);

            StepVerifier.create(result)
                    .assertNext(paginatedData -> {
                        assertThat(paginatedData.getContent()).hasSize(1);
                        assertThat(paginatedData.getContent().get(0).getClientName()).isEqualTo("Test User");
                        assertThat(paginatedData.getTotalElements()).isEqualTo(1L);
                        assertThat(paginatedData.getCurrentPage()).isZero();
                    })
                    .verifyComplete();
        }

        @Test
        void withNoClientFilters_shouldFetchSolicitudesFirstAndReturnPaginatedData() {
            when(repository.findSolicitudeReport(filterWithoutClientData)).thenReturn(Flux.just(partialSolicitudeReport));
            when(userPort.getUsersByEmails(anyList())).thenReturn(Flux.just(userProjection1));
            when(repository.countSolicitudeReport(filterWithoutClientData)).thenReturn(Mono.just(1L));

            Mono<PaginatedData<SolicitudeReport>> result = useCase.getSolicitudeReport(filterWithoutClientData);

            StepVerifier.create(result)
                    .assertNext(paginatedData -> {
                        assertThat(paginatedData.getContent()).hasSize(1);
                        assertThat(paginatedData.getContent().get(0).getClientName()).isEqualTo("Test User");
                        assertThat(paginatedData.getTotalElements()).isEqualTo(1L);
                    })
                    .verifyComplete();
        }

        @Test
        void withClientFilters_andNoUsersFound_shouldReturnEmptyMono() {
            when(userPort.getUserByFilter(filterWithClientData)).thenReturn(Flux.empty());

            Mono<PaginatedData<SolicitudeReport>> result = useCase.getSolicitudeReport(filterWithClientData);

            StepVerifier.create(result)
                    .assertNext(paginatedData -> assertThat(paginatedData.getContent()).isEmpty())
                    .verifyComplete();
        }

        @Test
        void withNoClientFilters_andNoSolicitudesFound_shouldReturnEmptyMono() {
            when(repository.findSolicitudeReport(filterWithoutClientData)).thenReturn(Flux.empty());

            Mono<PaginatedData<SolicitudeReport>> result = useCase.getSolicitudeReport(filterWithoutClientData);

            StepVerifier.create(result)
                    .assertNext(paginatedData -> assertThat(paginatedData.getContent()).isEmpty())
                    .verifyComplete();
        }

        @Test
        void withNoClientFilters_andUserNotFound_shouldReturnReportWithNullUserData() {
            when(repository.findSolicitudeReport(filterWithoutClientData)).thenReturn(Flux.just(partialSolicitudeReport));
            when(userPort.getUsersByEmails(anyList())).thenReturn(Flux.empty()); // User not found
            when(repository.countSolicitudeReport(filterWithoutClientData)).thenReturn(Mono.just(1L));

            Mono<PaginatedData<SolicitudeReport>> result = useCase.getSolicitudeReport(filterWithoutClientData);

            StepVerifier.create(result)
                    .assertNext(paginatedData -> {
                        assertThat(paginatedData.getContent()).hasSize(1);
                        assertThat(paginatedData.getContent().get(0).getClientName()).isNull(); // Assert user data is null
                    })
                    .verifyComplete();
        }
    }
}