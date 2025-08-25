package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.StateEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StateEntityRepository extends ReactiveCrudRepository<StateEntity, Integer>, ReactiveQueryByExampleExecutor<StateEntity> {

}
