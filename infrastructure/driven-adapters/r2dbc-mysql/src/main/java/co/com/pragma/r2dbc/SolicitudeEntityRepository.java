package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import co.com.pragma.r2dbc.reports.SolicitudeEntityRepositoryCustom;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SolicitudeEntityRepository extends
        ReactiveCrudRepository<SolicitudeEntity, Integer>,
        ReactiveQueryByExampleExecutor<SolicitudeEntity>,
        SolicitudeEntityRepositoryCustom {

    Flux<SolicitudeEntity> findByEmail(String email);
}
