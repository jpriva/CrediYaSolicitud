package co.com.pragma.model.solicitude.gateways;

import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface SolicitudeRepository {
    Mono<Solicitude> save(Solicitude solicitudeResponse);

    Flux<SolicitudeReport> findSolicitudeReport(SolicitudeReportFilter filter);

    Mono<BigDecimal> findTotalMonthlyFee(String email);

    Mono<Long> countSolicitudeReport(SolicitudeReportFilter filter);

    Mono<Solicitude> findById(Integer solicitudeId);
}
