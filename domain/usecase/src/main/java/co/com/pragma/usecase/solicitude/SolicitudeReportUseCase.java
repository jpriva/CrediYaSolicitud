package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import co.com.pragma.usecase.solicitude.utils.ReportUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<SolicitudeReport> getSolicitudeReport(SolicitudeReportFilter filter) {
        return Mono.just(filter)
                .flatMapMany(solicitudeFilter ->
                        ReportUtils.hasClientFilters(solicitudeFilter)
                                ? getUsersFirst(solicitudeFilter)
                                : getSolicitudesFirst(solicitudeFilter)
                );
    }

    private Flux<SolicitudeReport> getUsersFirst(SolicitudeReportFilter filter) {
        logger.info("Client filters detected. Fetching users first.");
        return userPort.getUserByFilter(filter)
                .collectMap(UserProjection::getEmail)
                .filter(map -> !map.isEmpty())
                .flatMapMany(usersMap ->
                        repository.findSolicitudeReport(
                                        filter.toBuilder().emailsIn(usersMap.keySet().stream().toList()
                                        ).build())
                                .map(report ->
                                        ReportUtils.buildReport(report, usersMap.get(report.getClientEmail()))
                                )
                );
    }

    private Flux<SolicitudeReport> getSolicitudesFirst(SolicitudeReportFilter filter) {
        logger.info("No client filters detected. Fetching solicitudes first.");
        return repository.findSolicitudeReport(filter)
                .collectList()
                .filter(solicitudes -> !solicitudes.isEmpty())
                .flatMapMany(solicitudes ->
                        ReportUtils.getUserData(userPort, solicitudes)
                )
                .doOnError(ex -> logger.error("Error getting solicitude report", ex))
                .doOnNext(report -> logger.debug("Solicitude Report: {}", report));
    }
}
