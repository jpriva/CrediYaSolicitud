package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.SolicitudeNullException;
import co.com.pragma.model.solicitude.exceptions.ValueOutOfBoundsException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.exceptions.StateNotFoundException;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.UserNotFoundException;
import co.com.pragma.model.user.gateways.UserPort;
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
import static org.mockito.ArgumentMatchers.anyString;
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
    private UserPort userPort;
    @Mock
    private LoggerPort logger;
    @Mock
    private TransactionalPort transactionalPort;

    @InjectMocks
    private SolicitudeUseCase solicitudeUseCase;

    private Solicitude testSolicitude;
    private User testUser;
    private LoanType testLoanType;
    private State testState;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .idNumber("12345")
                .email("test@example.com")
                .build();

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
        Solicitude savedSolicitude = testSolicitude.toBuilder()
                .solicitudeId(100)
                .email(testUser.getEmail())
                .loanType(testLoanType)
                .state(testState)
                .build();

        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.just(testUser));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));
        when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.just(savedSolicitude));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345"))
                .expectNextMatches(result ->
                        result.getSolicitudeId() == 100 &&
                        result.getEmail().equals("test@example.com") &&
                        result.getLoanType().getLoanTypeId() == 1 &&
                        result.getState().getStateId() == 1
                )
                .verifyComplete();
    }

    @Test
    void shouldFailWhenInputSolicitudeIsNull() {
        StepVerifier.create(solicitudeUseCase.saveSolicitude(null, "12345"))
                .expectError(SolicitudeNullException.class)
                .verify();
    }

    @Test
    void shouldFailWhenUserIsNotFound() {
        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.error(new UserNotFoundException("User not found","code")));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenLoanTypeIsNotFound() {
        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.just(testUser));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.empty());
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345"))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenStateIsNotFound() {
        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.just(testUser));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345"))
                .expectError(StateNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenValueIsBelowLoanTypeMinimum() {
        Solicitude invalidSolicitude = testSolicitude.toBuilder()
                .value(new BigDecimal("500.00"))
                .build();

        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.just(testUser));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(invalidSolicitude, "12345"))
                .expectError(ValueOutOfBoundsException.class)
                .verify();
    }

    @Test
    void shouldFailWhenValueIsAboveLoanTypeMaximum() {
        Solicitude invalidSolicitude = testSolicitude.toBuilder()
                .value(new BigDecimal("25000.00"))
                .build();

        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.just(testUser));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(invalidSolicitude, "12345"))
                .expectError(ValueOutOfBoundsException.class)
                .verify();
    }

    @Test
    void shouldFailWhenRepositorySaveFails() {
        RuntimeException dbException = new RuntimeException("Database connection failed");

        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.just(testUser));
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(testState));
        when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.error(dbException));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345"))
                .expectErrorMatches(error -> error instanceof RuntimeException && "Database connection failed".equals(error.getMessage()))
                .verify();
    }

    @Test
    void shouldFailWhenFieldValidationFails() {
        Solicitude invalidSolicitude = testSolicitude.toBuilder()
                .deadline(0)
                .build();

        when(userPort.getUserByIdNumber(anyString())).thenReturn(Mono.just(testUser));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(invalidSolicitude, "12345"))
                .expectError()
                .verify();
    }
}