package co.com.pragma.model.solicitude.gateways;

import co.com.pragma.model.solicitude.Solicitude;
import reactor.core.publisher.Mono;

public interface SolicitudeRepository {
    Mono<Solicitude> save(Solicitude solicitudeResponse);
}
