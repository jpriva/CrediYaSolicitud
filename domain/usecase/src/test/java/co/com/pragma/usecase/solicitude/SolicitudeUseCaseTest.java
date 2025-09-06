package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.exceptions.InvalidFieldException;
import co.com.pragma.model.exceptions.ValueOutOfBoundsException;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.SolicitudeNullException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.exceptions.StateNotFoundException;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudeUseCaseTest {

    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private SolicitudeRepository solicitudeRepository;
    @Mock
    private LoggerPort logger;
    @Mock
    private TransactionalPort transactionalPort;

    @InjectMocks
    private SolicitudeUseCase solicitudeUseCase;

    private Solicitude testSolicitude;
    private LoanType testLoanType;
    private State testState;
    private JwtData testJwtData;

    @BeforeEach
    void setUp() {
        testJwtData = new JwtData("test@example.com", "CLIENTE", 1, "Test", "12345");

        testLoanType = LoanType.builder()
                .loanTypeId(1)
                .minValue(new BigDecimal("1000.00"))
                .maxValue(new BigDecimal("20000.00"))
                .build();

        testState = State.builder()
                .stateId(1)
                .name(DefaultValues.PENDING_STATE)
                .build();

        testSolicitude = Solicitude.builder()
                .value(new BigDecimal("5000.00"))
                .deadline(12)
                .loanType(LoanType.builder().loanTypeId(1).build())
                .build();

        when(transactionalPort.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldSaveSolicitudeSuccessfully() {

        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));
        when(solicitudeRepository.save(any(Solicitude.class))).thenAnswer(invocation -> {
            Solicitude input = invocation.getArgument(0);
            return Mono.just(input.toBuilder().solicitudeId(100).build());
        });

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345", testJwtData))
                .expectNextMatches(result ->
                        result.getSolicitudeId() == 100 &&
                                result.getEmail().equals(testJwtData.subject()) &&
                                result.getLoanType().getLoanTypeId() == 1 &&
                                result.getState().getStateId() == 1
                )
                .verifyComplete();
    }

    @Test
    void shouldFailWhenInputSolicitudeIsNull() {
        StepVerifier.create(solicitudeUseCase.saveSolicitude(null, "12345", testJwtData))
                .expectError(SolicitudeNullException.class)
                .verify();
    }

    @Test
    void shouldFailWhenIdNumberDoesNotMatchToken() {
        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "differentIdNumber", testJwtData))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void shouldFailWhenLoanTypeIsNotFound() {
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.empty());
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345", testJwtData))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenStateIsNotFound() {
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345", testJwtData))
                .expectError(StateNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenValueIsBelowLoanTypeMinimum() {
        Solicitude invalidSolicitude = testSolicitude.toBuilder()
                .value(new BigDecimal("500.00"))
                .build();

        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(invalidSolicitude, "12345", testJwtData))
                .expectError(ValueOutOfBoundsException.class)
                .verify();
    }

    @Test
    void shouldFailWhenValueIsAboveLoanTypeMaximum() {
        Solicitude invalidSolicitude = testSolicitude.toBuilder()
                .value(new BigDecimal("25000.00"))
                .build();

        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(invalidSolicitude, "12345", testJwtData))
                .expectError(ValueOutOfBoundsException.class)
                .verify();
    }

    @Test
    void shouldFailWhenRepositorySaveFails() {
        RuntimeException dbException = new RuntimeException("Database connection failed");

        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));
        when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345", testJwtData))
                .expectErrorMatches(error -> error instanceof RuntimeException && "Database connection failed".equals(error.getMessage()))
                .verify();
    }

    @Test
    void shouldFailWhenFieldValidationFails() {
        Solicitude invalidSolicitude = testSolicitude.toBuilder()
                .deadline(0)
                .build();


        StepVerifier.create(solicitudeUseCase.saveSolicitude(invalidSolicitude, "12345", testJwtData))
                .expectError()
                .verify();
    }
}