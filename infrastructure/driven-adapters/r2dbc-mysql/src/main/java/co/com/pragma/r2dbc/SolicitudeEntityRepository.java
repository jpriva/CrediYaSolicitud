package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SolicitudeEntityRepository extends ReactiveCrudRepository<SolicitudeEntity, Integer>, ReactiveQueryByExampleExecutor<SolicitudeEntity> {
}
