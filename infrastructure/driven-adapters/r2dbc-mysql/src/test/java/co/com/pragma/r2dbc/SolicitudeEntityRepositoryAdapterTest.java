package co.com.pragma.r2dbc;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import co.com.pragma.r2dbc.mapper.PersistenceSolicitudeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private Solicitude domainSolicitude;
    private SolicitudeEntity entitySolicitude;

    @BeforeEach
    void setUp() {
        domainSolicitude = Solicitude.builder()
                .solicitudeId(1)
                .value(BigDecimal.valueOf(1000))
                .build();

        entitySolicitude = new SolicitudeEntity();
        entitySolicitude.setSolicitudeId(1);
        entitySolicitude.setValue(BigDecimal.valueOf(1000));
    }

    @Nested
    class SaveTests {
        @Test
        void shouldSaveAndReturnMappedDomainObject() {
            when(solicitudeMapper.toEntity(any(Solicitude.class))).thenReturn(entitySolicitude);
            when(solicitudeRepository.save(any(SolicitudeEntity.class))).thenReturn(Mono.just(entitySolicitude));
            when(solicitudeMapper.toDomain(any(SolicitudeEntity.class))).thenReturn(domainSolicitude);

            Mono<Solicitude> result = adapter.save(domainSolicitude);

            StepVerifier.create(result)
                    .expectNext(domainSolicitude)
                    .verifyComplete();

            verify(solicitudeMapper).toEntity(domainSolicitude);
            verify(solicitudeRepository).save(entitySolicitude);
            verify(solicitudeMapper).toDomain(entitySolicitude);
        }

        @Test
        void shouldReturnErrorWhenSaveFails() {
            // Arrange
            RuntimeException dbException = new RuntimeException("DB Error on save");
            when(solicitudeMapper.toEntity(any(Solicitude.class))).thenReturn(entitySolicitude);
            when(solicitudeRepository.save(any(SolicitudeEntity.class))).thenReturn(Mono.error(dbException));

            // Act
            Mono<Solicitude> result = adapter.save(domainSolicitude);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error on save".equals(throwable.getMessage()))
                    .verify();
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void shouldReturnDomainObjectWhenFound() {
            when(solicitudeRepository.findById(1)).thenReturn(Mono.just(entitySolicitude));
            when(solicitudeMapper.toDomain(any(SolicitudeEntity.class))).thenReturn(domainSolicitude);

            Mono<Solicitude> result = adapter.findById(1);

            StepVerifier.create(result)
                    .expectNext(domainSolicitude)
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            when(solicitudeRepository.findById(2)).thenReturn(Mono.empty());

            Mono<Solicitude> result = adapter.findById(2);

            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        void shouldReturnErrorWhenFindByIdFails() {
            // Arrange
            RuntimeException dbException = new RuntimeException("DB Error on find");
            when(solicitudeRepository.findById(anyInt())).thenReturn(Mono.error(dbException));

            // Act
            Mono<Solicitude> result = adapter.findById(99);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error on find".equals(throwable.getMessage()))
                    .verify();
        }
    }

    @Nested
    class FindSolicitudeReportTests {
        @Test
        void shouldDelegateToRepositoryAndReturnFlux() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().stateName("PENDIENTE").build();
            SolicitudeReport reportItem = SolicitudeReport.builder().solicitudeId(1).stateName("PENDIENTE").build();
            when(solicitudeRepository.findSolicitudeReport(filter)).thenReturn(Flux.just(reportItem));

            Flux<SolicitudeReport> result = adapter.findSolicitudeReport(filter);

            StepVerifier.create(result)
                    .expectNext(reportItem)
                    .verifyComplete();

            verify(solicitudeRepository).findSolicitudeReport(filter);
        }

        @Test
        void shouldReturnEmptyFluxWhenRepositoryReturnsEmpty() {
            // Arrange
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().build();
            when(solicitudeRepository.findSolicitudeReport(filter)).thenReturn(Flux.empty());

            // Act
            Flux<SolicitudeReport> result = adapter.findSolicitudeReport(filter);

            // Assert
            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        void shouldReturnErrorWhenFindSolicitudeReportFails() {
            // Arrange
            RuntimeException dbException = new RuntimeException("DB Error on report");
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().build();
            when(solicitudeRepository.findSolicitudeReport(filter)).thenReturn(Flux.error(dbException));

            // Act
            Flux<SolicitudeReport> result = adapter.findSolicitudeReport(filter);

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error on report".equals(throwable.getMessage()))
                    .verify();
        }
    }
}