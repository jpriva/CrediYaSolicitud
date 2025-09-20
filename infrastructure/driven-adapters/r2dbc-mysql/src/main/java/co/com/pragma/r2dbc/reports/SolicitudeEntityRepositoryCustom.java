package co.com.pragma.r2dbc.reports;

import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface SolicitudeEntityRepositoryCustom {
    Flux<SolicitudeReport> findSolicitudeReport(SolicitudeReportFilter filter);

    Mono<Long> countSolicitudeReport(SolicitudeReportFilter filter);

    Mono<BigDecimal> findTotalMonthlyFee(String email);
}
