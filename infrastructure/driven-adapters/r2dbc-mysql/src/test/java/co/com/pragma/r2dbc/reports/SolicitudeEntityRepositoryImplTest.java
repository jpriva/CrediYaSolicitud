package co.com.pragma.r2dbc.reports;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.page.PageableData;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import io.r2dbc.spi.Readable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudeEntityRepositoryImplTest {

    @Mock
    DatabaseClient databaseClient;

    @Mock
    LoggerPort logger;

    @Mock
    DatabaseClient.GenericExecuteSpec executeSpec;

    @Mock
    RowsFetchSpec<SolicitudeReport> rowsFetchSpecReports;

    @Mock
    RowsFetchSpec<Long> rowsFetchSpecCount;

    @InjectMocks
    SolicitudeEntityRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(databaseClient.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.bind(anyString(), any())).thenReturn(executeSpec);
    }

    @Test
    void findSolicitudeReport_shouldReturnMappedFluxAndBindLimitOffset() {
        PageableData pageable = PageableData.builder()
                .page(1)
                .size(20)
                .sortBy("monto")
                .sortDirection("ASC")
                .build();
        SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                .pageable(pageable)
                .build();

        SolicitudeReport r1 = SolicitudeReport.builder()
                .solicitudeId(1)
                .value(new BigDecimal("1000.00"))
                .deadline(12)
                .clientEmail("a@b.com")
                .stateName("Approved")
                .loanTypeName("Personal")
                .interestRate(new BigDecimal("1.5"))
                .totalMonthlyPay(new BigDecimal("120.00"))
                .build();
        SolicitudeReport r2 = r1.toBuilder().solicitudeId(2).clientEmail("c@d.com").build();

        when(executeSpec.map(any(BiFunction.class))).thenReturn(rowsFetchSpecReports);
        when(rowsFetchSpecReports.all()).thenReturn(Flux.just(r1, r2));

        StepVerifier.create(repository.findSolicitudeReport(filter))
                .expectNext(r1)
                .expectNext(r2)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        String usedSql = sqlCaptor.getValue();
        assertThat(usedSql).contains("SELECT").contains("FROM solicitud s").contains("LIMIT :limit OFFSET :offset");

        verify(executeSpec, atLeastOnce()).bind("limit", 20);
        verify(executeSpec, atLeastOnce()).bind("offset", 20L);

        verify(logger, times(2)).debug(anyString(), any());
    }

    @Test
    void countSolicitudeReport_shouldReturnCount() {
        PageableData pageable = PageableData.builder()
                .page(0)
                .size(10)
                .build();
        SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                .pageable(pageable)
                .build();

        when(executeSpec.map((Function<Readable, Long>) any())).thenReturn(rowsFetchSpecCount);
        when(rowsFetchSpecCount.one()).thenReturn(Mono.just(5L));

        StepVerifier.create(repository.countSolicitudeReport(filter))
                .expectNext(5L)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue()).startsWith("SELECT COUNT(s.id_solicitud)");
    }

    @Test
    void countSolicitudeReport_whenEmptyShouldReturnZero() {
        SolicitudeReportFilter filter = SolicitudeReportFilter.builder().build();
        when(executeSpec.map((Function<Readable, Long>) any())).thenReturn(rowsFetchSpecCount);
        when(rowsFetchSpecCount.one()).thenReturn(Mono.empty());

        StepVerifier.create(repository.countSolicitudeReport(filter))
                .expectNext(0L)
                .verifyComplete();
    }
}
