package co.com.pragma.usecase.loantype;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanTypeUseCaseTest {

    /**
     * Unit tests for the getAllLoanTypes method in LoanTypeUseCase.
     * This method is responsible for retrieving all loan types from the repository.
     */

    @Test
    void getAllLoanTypes_shouldReturnLoanTypes_whenRepositoryReturnsLoanTypes() {
        // Arrange
        LoanTypeRepository loanTypeRepository = mock(LoanTypeRepository.class);
        LoanTypeUseCase loanTypeUseCase = new LoanTypeUseCase(loanTypeRepository);

        LoanType loanType1 = LoanType.builder().build();
        LoanType loanType2 = LoanType.builder().build();
        when(loanTypeRepository.findAll()).thenReturn(Flux.just(loanType1, loanType2));

        // Act
        Flux<LoanType> result = loanTypeUseCase.getAllLoanTypes();

        // Assert
        StepVerifier.create(result)
                .expectNext(loanType1)
                .expectNext(loanType2)
                .verifyComplete();
    }

    @Test
    void getAllLoanTypes_shouldReturnEmpty_whenRepositoryReturnsEmpty() {
        // Arrange
        LoanTypeRepository loanTypeRepository = mock(LoanTypeRepository.class);
        LoanTypeUseCase loanTypeUseCase = new LoanTypeUseCase(loanTypeRepository);

        when(loanTypeRepository.findAll()).thenReturn(Flux.empty());

        // Act
        Flux<LoanType> result = loanTypeUseCase.getAllLoanTypes();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllLoanTypes_shouldError_whenRepositoryErrors() {
        // Arrange
        LoanTypeRepository loanTypeRepository = mock(LoanTypeRepository.class);
        LoanTypeUseCase loanTypeUseCase = new LoanTypeUseCase(loanTypeRepository);

        RuntimeException exception = new RuntimeException("Database error");
        when(loanTypeRepository.findAll()).thenReturn(Flux.error(exception));

        // Act
        Flux<LoanType> result = loanTypeUseCase.getAllLoanTypes();

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();
    }
}