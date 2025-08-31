package co.com.pragma.r2dbc;

import co.com.pragma.model.state.State;
import co.com.pragma.r2dbc.entity.StateEntity;
import co.com.pragma.r2dbc.mapper.PersistenceStateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateEntityRepositoryAdapterTest {

    @Mock
    private StateEntityRepository stateRepository;

    @Mock
    private PersistenceStateMapper stateMapper;

    @InjectMocks
    private StateEntityRepositoryAdapter adapter;

    @Test
    void shouldReturnStateWhenFound() {
        State inputDomain = State.builder().name("PENDIENTE").build();
        StateEntity entityToFind = new StateEntity();
        entityToFind.setName("PENDIENTE");

        StateEntity foundEntity = new StateEntity();
        foundEntity.setStateId(1);
        foundEntity.setName("PENDIENTE");

        State expectedDomain = State.builder().stateId(1).name("PENDIENTE").build();

        when(stateMapper.toEntity(inputDomain)).thenReturn(entityToFind);
        when(stateRepository.findOne(any(Example.class))).thenReturn(Mono.just(foundEntity));
        when(stateMapper.toDomain(foundEntity)).thenReturn(expectedDomain);

        Mono<State> result = adapter.findOne(inputDomain);

        StepVerifier.create(result)
                .expectNext(expectedDomain)
                .verifyComplete();

        verify(stateRepository).findOne(any(Example.class));
        verify(stateMapper).toDomain(foundEntity);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        State inputDomain = State.builder().name("NO_EXISTE").build();
        StateEntity entityToFind = new StateEntity();
        entityToFind.setName("NO_EXISTE");

        when(stateMapper.toEntity(inputDomain)).thenReturn(entityToFind);
        when(stateRepository.findOne(any(Example.class))).thenReturn(Mono.empty());

        Mono<State> result = adapter.findOne(inputDomain);

        StepVerifier.create(result)
                .verifyComplete();

        verify(stateMapper, never()).toDomain(any());
    }

    @Test
    void shouldReturnErrorWhenRepositoryFails() {
        State inputDomain = State.builder().name("ERROR").build();
        RuntimeException dbException = new RuntimeException("Database error");

        when(stateMapper.toEntity(inputDomain)).thenReturn(new StateEntity());
        when(stateRepository.findOne(any(Example.class))).thenReturn(Mono.error(dbException));

        Mono<State> result = adapter.findOne(inputDomain);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "Database error".equals(throwable.getMessage()))
                .verify();
    }
}