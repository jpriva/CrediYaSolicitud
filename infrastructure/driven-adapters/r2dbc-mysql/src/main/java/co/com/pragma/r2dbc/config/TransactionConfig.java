package co.com.pragma.r2dbc.config;

import co.com.pragma.model.transaction.gateways.TransactionalPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public TransactionalPort transactionalPort(ReactiveTransactionManager transactionManager){
        return new TransactionalPort() {
            @Override
            public <T> Mono<T> transactional(Mono<T> mono) {
                TransactionalOperator transactionalOperator = TransactionalOperator.create(transactionManager);
                return mono.as(transactionalOperator::transactional);
            }

            @Override
            public <T> Flux<T> transactional(Flux<T> flux) {
                TransactionalOperator transactionalOperator = TransactionalOperator.create(transactionManager);
                return transactionalOperator.transactional(flux);
            }
        };
    }
}