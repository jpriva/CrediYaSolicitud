package co.com.pragma.r2dbc;

import co.com.pragma.model.state.State;
import co.com.pragma.r2dbc.entity.StateEntity;
import co.com.pragma.r2dbc.mapper.PersistenceStateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateEntityRepositoryAdapterTest {

    @Mock
    private StateEntityRepository stateRepository;

    @Mock
    private PersistenceStateMapper stateMapper;

    @InjectMocks
    private StateEntityRepositoryAdapter adapter;

    private State domainState;
    private StateEntity entityState;

    @BeforeEach
    void setUp() {
        domainState = State.builder().stateId(1).name("PENDING").description("Pending state").build();
        entityState = new StateEntity();
        entityState.setStateId(1);
        entityState.setName("PENDING");
        entityState.setDescription("Pending state");
    }

    @Nested
    class FindOneTests {
        @Test
        void shouldReturnDomainObjectWhenFound() {
            when(stateMapper.toEntity(any(State.class))).thenReturn(entityState);
            when(stateRepository.findOne(any(Example.class))).thenReturn(Mono.just(entityState));
            when(stateMapper.toDomain(any(StateEntity.class))).thenReturn(domainState);

            Mono<State> result = adapter.findOne(domainState);

            StepVerifier.create(result)
                    .expectNext(domainState)
                    .verifyComplete();

            verify(stateMapper).toEntity(domainState);
            verify(stateRepository).findOne(any(Example.class));
            verify(stateMapper).toDomain(entityState);
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            when(stateMapper.toEntity(any(State.class))).thenReturn(entityState);
            when(stateRepository.findOne(any(Example.class))).thenReturn(Mono.empty());

            Mono<State> result = adapter.findOne(domainState);

            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    class FindAllTests {
        @Test
        void shouldReturnAllStatesFromRepository() {
            when(stateRepository.findAll()).thenReturn(Flux.just(entityState));
            when(stateMapper.toDomain(any(StateEntity.class))).thenReturn(domainState);

            Flux<State> result = adapter.findAll();

            StepVerifier.create(result)
                    .expectNext(domainState)
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenRepositoryIsEmpty() {
            when(stateRepository.findAll()).thenReturn(Flux.empty());

            Flux<State> result = adapter.findAll();

            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    class SaveAllTests {
        @Test
        void shouldSaveAndReturnMappedDomainObjects() {
            List<State> domainStates = List.of(domainState);
            List<StateEntity> entityStates = List.of(entityState);

            when(stateMapper.toEntity(domainState)).thenReturn(entityState);
            when(stateRepository.saveAll(any(List.class))).thenReturn(Flux.fromIterable(entityStates));
            when(stateMapper.toDomain(entityState)).thenReturn(domainState);

            Flux<State> result = adapter.saveAll(domainStates);

            StepVerifier.create(result)
                    .expectNext(domainState)
                    .verifyComplete();
        }

        @Test
        void shouldHandleEmptyList() {
            List<State> emptyList = Collections.emptyList();
            when(stateRepository.saveAll(any(List.class))).thenReturn(Flux.empty());

            Flux<State> result = adapter.saveAll(emptyList);

            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    class FindByNameTests {
        @Test
        void shouldReturnDomainObjectWhenFound() {
            String name = "PENDING";
            when(stateRepository.findByName(name)).thenReturn(Mono.just(entityState));
            when(stateMapper.toDomain(entityState)).thenReturn(domainState);

            Mono<State> result = adapter.findByName(name);

            StepVerifier.create(result)
                    .expectNext(domainState)
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            String name = "UNKNOWN";
            when(stateRepository.findByName(name)).thenReturn(Mono.empty());

            Mono<State> result = adapter.findByName(name);

            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        void shouldPropagateErrorWhenRepositoryFails() {
            String name = "PENDING";
            RuntimeException exception = new RuntimeException("DB Error");
            when(stateRepository.findByName(name)).thenReturn(Mono.error(exception));

            Mono<State> result = adapter.findByName(name);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error".equals(throwable.getMessage()))
                    .verify();
        }
    }
}