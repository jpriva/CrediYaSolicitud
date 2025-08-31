package co.com.pragma.r2dbc;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.r2dbc.mapper.PersistenceLoanTypeMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanTypeEntityRepositoryAdapterTest {

    @Mock
    private LoanTypeEntityRepository loanTypeRepository;

    @Mock
    private PersistenceLoanTypeMapper loanTypeMapper;

    @InjectMocks
    private LoanTypeEntityRepositoryAdapter adapter;

    @Nested
    class FindAllTests {
        @Test
        void shouldReturnAllLoanTypes() {
            LoanTypeEntity entity1 = new LoanTypeEntity();
            entity1.setLoanTypeId(1);
            LoanTypeEntity entity2 = new LoanTypeEntity();
            entity2.setLoanTypeId(2);

            LoanType domain1 = LoanType.builder().loanTypeId(1).build();
            LoanType domain2 = LoanType.builder().loanTypeId(2).build();

            when(loanTypeRepository.findAll()).thenReturn(Flux.just(entity1, entity2));
            when(loanTypeMapper.toDomain(entity1)).thenReturn(domain1);
            when(loanTypeMapper.toDomain(entity2)).thenReturn(domain2);

            Flux<LoanType> result = adapter.findAll();

            StepVerifier.create(result)
                    .expectNext(domain1, domain2)
                    .verifyComplete();
        }

        @Test
        void shouldReturnErrorWhenFindAllFails() {
            RuntimeException dbException = new RuntimeException("DB Error");
            when(loanTypeRepository.findAll()).thenReturn(Flux.error(dbException));

            Flux<LoanType> result = adapter.findAll();

            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error".equals(throwable.getMessage()))
                    .verify();
        }
    }

    @Nested
    class FindByIdTests {
        @Test
        void shouldReturnLoanTypeWhenFound() {
            LoanTypeEntity foundEntity = new LoanTypeEntity();
            foundEntity.setLoanTypeId(1);
            LoanType expectedDomain = LoanType.builder().loanTypeId(1).build();

            when(loanTypeRepository.findById(1)).thenReturn(Mono.just(foundEntity));
            when(loanTypeMapper.toDomain(foundEntity)).thenReturn(expectedDomain);

            Mono<LoanType> result = adapter.findById(1);

            StepVerifier.create(result)
                    .expectNext(expectedDomain)
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenNotFound() {
            when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.empty());

            Mono<LoanType> result = adapter.findById(99);

            StepVerifier.create(result)
                    .verifyComplete();
            verify(loanTypeMapper, never()).toDomain(any());
        }

        @Test
        void shouldReturnErrorWhenFindByIdFails() {
            RuntimeException dbException = new RuntimeException("DB Error");
            when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.error(dbException));

            Mono<LoanType> result = adapter.findById(99);

            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error".equals(throwable.getMessage()))
                    .verify();
        }
    }
}