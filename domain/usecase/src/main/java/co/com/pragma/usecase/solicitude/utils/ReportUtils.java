package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReportUtils {
    public static SolicitudeReport buildReport(SolicitudeReport solicitude, UserProjection user) {
        return solicitude.toBuilder()
                .clientName(user != null ? user.getName() : null)
                .clientIdentification(user != null ? user.getIdNumber() : null)
                .baseSalary(user != null ? user.getBaseSalary() : null)
                .build();
    }


    public static Flux<SolicitudeReport> getUserData(
            UserPort userPort,
            List<SolicitudeReport> solicitudes
    ) {
        return getMapOfUsersFromService(userPort, solicitudes)
                .flatMapMany(usersMap ->
                        combineUsersAndSolicitudesIntoFlux(solicitudes, usersMap)
                );
    }

    private static Mono<Map<String, UserProjection>> getMapOfUsersFromService(
            UserPort userPort,
            List<SolicitudeReport> solicitudes
    ) {
        return userPort.getUsersByEmails(
                        solicitudes.stream()
                                .map(SolicitudeReport::getClientEmail)
                                .distinct()
                                .toList()
                )
                .collectMap(UserProjection::getEmail);
    }

    private static Flux<SolicitudeReport> combineUsersAndSolicitudesIntoFlux(
            List<SolicitudeReport> solicitudes,
            Map<String, UserProjection> usersMap
    ) {
        return Flux.fromIterable(solicitudes)
                .map(solicitude ->
                        ReportUtils.buildReport(
                                solicitude,
                                usersMap.get(solicitude.getClientEmail())
                        )
                );
    }
}
