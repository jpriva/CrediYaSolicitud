package co.com.pragma.r2dbc;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import co.com.pragma.r2dbc.mapper.PersistenceSolicitudeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudeEntityRepositoryAdapterTest {

    @Mock
    private SolicitudeEntityRepository solicitudeRepository;

    @Mock
    private PersistenceSolicitudeMapper solicitudeMapper;

    @Mock
    private LoggerPort logger;

    @InjectMocks
    private SolicitudeEntityRepositoryAdapter adapter;

    @Test
    void shouldSaveAndReturnDomainObjectOnSuccess() {
        Solicitude inputDomain = Solicitude.builder().email("test@test.com").build();
        SolicitudeEntity entityToSave = new SolicitudeEntity();
        SolicitudeEntity savedEntity = new SolicitudeEntity();
        savedEntity.setSolicitudeId(1);
        Solicitude expectedDomain = Solicitude.builder().solicitudeId(1).email("test@test.com").build();

        when(solicitudeMapper.toEntity(inputDomain)).thenReturn(entityToSave);
        when(solicitudeRepository.save(entityToSave)).thenReturn(Mono.just(savedEntity));
        when(solicitudeMapper.toDomain(savedEntity)).thenReturn(expectedDomain);

        Mono<Solicitude> result = adapter.save(inputDomain);

        StepVerifier.create(result)
                .expectNext(expectedDomain)
                .verifyComplete();

        verify(solicitudeMapper).toEntity(inputDomain);
        verify(solicitudeRepository).save(entityToSave);
        verify(solicitudeMapper).toDomain(savedEntity);
    }

    @Test
    void shouldReturnErrorWhenRepositoryFails() {
        Solicitude inputDomain = Solicitude.builder().email("test@test.com").build();
        SolicitudeEntity entityToSave = new SolicitudeEntity();
        RuntimeException dbException = new RuntimeException("Database connection error");

        when(solicitudeMapper.toEntity(inputDomain)).thenReturn(entityToSave);
        when(solicitudeRepository.save(entityToSave)).thenReturn(Mono.error(dbException));

        Mono<Solicitude> result = adapter.save(inputDomain);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        "Database connection error".equals(throwable.getMessage()))
                .verify();

        verify(solicitudeMapper, never()).toDomain(any());
    }
}