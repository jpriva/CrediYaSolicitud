package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.page.PaginatedData;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import co.com.pragma.usecase.solicitude.utils.ReportUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class SolicitudeReportUseCase {

    private final SolicitudeRepository repository;
    private final UserPort userPort;
    private final LoggerPort logger;

    //N+1 problem, just for didactic proposes. Make a call for every loan application to the user's microservice
    public Flux<SolicitudeReport> getSolicitudeReportReactive(SolicitudeReportFilter filter) {
        return repository.findSolicitudeReport(filter)
                .flatMap(solicitudeProjection ->
                        userPort.getUserByEmail(solicitudeProjection.getClientEmail())
                                .map(userProjection -> ReportUtils.buildReport(solicitudeProjection, userProjection))
                                .defaultIfEmpty(ReportUtils.buildReport(solicitudeProjection, null))
                )
                .doOnError(ex -> logger.error("Error getting solicitude report", ex))
                .doOnNext(report -> logger.debug("Solicitude Report: {}", report));
    }

    public Mono<PaginatedData<SolicitudeReport>> getSolicitudeReport(SolicitudeReportFilter filter) {
        return Mono.just(filter)
                .flatMap(solicitudeFilter ->
                        ReportUtils.hasClientFilters(solicitudeFilter)
                                ? getUsersFirst(solicitudeFilter)
                                : getSolicitudesFirst(solicitudeFilter)
                );
    }

    private Mono<PaginatedData<SolicitudeReport>> getUsersFirst(SolicitudeReportFilter filter) {
        logger.info("Client filters detected. Fetching users first.");
        return userPort.getUserByFilter(filter)
                .collectMap(UserProjection::getEmail)
                .flatMap(usersMap -> {
                    if (usersMap.isEmpty()) {
                        return Mono.just(ReportUtils.buildPaginated(filter, emptyList(), 0L));
                    }
                    SolicitudeReportFilter newFilter = filter.toBuilder()
                            .emailsIn(usersMap.keySet().stream().toList())
                            .build();
                    return repository.findSolicitudeReport(newFilter)
                            .map(report -> ReportUtils.buildReport(report, usersMap.get(report.getClientEmail())))
                            .collectList()
                            .flatMap(list -> getPaginatedReport(newFilter, list));
                });
    }

    private Mono<PaginatedData<SolicitudeReport>> getSolicitudesFirst(SolicitudeReportFilter filter) {
        logger.info("No client filters detected. Fetching solicitudes first.");
        return repository.findSolicitudeReport(filter)
                .collectList().flatMap(solicitudes -> {
                    if (solicitudes.isEmpty()) {
                        return Mono.just(ReportUtils.buildPaginated(filter, solicitudes, 0L));
                    }
                    return ReportUtils.getUserData(userPort, solicitudes)
                            .collectList()
                            .flatMap(list -> getPaginatedReport(filter, list));
                })
                .doOnError(ex -> logger.error("Error getting solicitude report", ex))
                .doOnNext(report -> logger.debug("Solicitude Report Page: {}", report));
    }

    private Mono<PaginatedData<SolicitudeReport>> getPaginatedReport(
            SolicitudeReportFilter filter,
            List<SolicitudeReport> list
    ) {
        return repository.countSolicitudeReport(filter)
                .map(size ->
                        ReportUtils.buildPaginated(filter, list, size)
                );
    }
}
