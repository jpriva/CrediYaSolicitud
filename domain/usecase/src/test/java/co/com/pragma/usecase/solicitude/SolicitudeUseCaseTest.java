package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.exceptions.InvalidFieldException;
import co.com.pragma.model.exceptions.ValueOutOfBoundsException;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeNotFoundException;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.SolicitudeNullException;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.sqs.gateways.SQSPort;
import co.com.pragma.model.state.State;
import co.com.pragma.model.state.exceptions.StateNotFoundException;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.template.EmailMessage;
import co.com.pragma.model.template.gateways.TemplatePort;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import co.com.pragma.usecase.solicitude.utils.NotificationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.*;

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
    @Mock
    private SQSPort sqsPort;
    @Mock
    private TemplatePort templatePort;
    @Mock
    private UserPort userPort;
    @Mock
    private JwtProviderPort jwtPort;

    @InjectMocks
    private SolicitudeUseCase solicitudeUseCase;

    private Solicitude testSolicitude;
    private LoanType testLoanType;
    private State pendingState;
    private State approvedState;
    private State rejectedState;
    private JwtData testJwtData;

    @BeforeEach
    void setUp() {
        testJwtData = new JwtData("test@example.com", "CLIENTE", 1, "Test", "12345");

        testLoanType = LoanType.builder()
                .loanTypeId(1)
                .minValue(new BigDecimal("1000.00"))
                .maxValue(new BigDecimal("20000.00"))
                .autoValidation(true)
                .interestRate(new BigDecimal("1.5"))
                .build();

        pendingState = State.builder()
                .stateId(1)
                .name(DefaultValues.PENDING_STATE)
                .build();

        approvedState = State.builder()
                .stateId(2)
                .name(DefaultValues.APPROVED_STATE)
                .build();

        rejectedState = State.builder()
                .stateId(3)
                .name(DefaultValues.REJECTED_STATE)
                .build();

        testSolicitude = Solicitude.builder()
                .value(new BigDecimal("5000.00"))
                .deadline(12)
                .loanType(LoanType.builder().loanTypeId(1).build())
                .build();

        lenient().when(transactionalPort.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldSaveSolicitudeSuccessfully() {

        UserProjection mockUser = UserProjection.builder()
                .email("test@example.com")
                .idNumber("12345")
                .name("Test User")
                .baseSalary(new BigDecimal("5000000"))
                .build();

        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(pendingState));
        when(solicitudeRepository.save(any(Solicitude.class))).thenAnswer(invocation -> {
            Solicitude input = invocation.getArgument(0);
            return Mono.just(input.toBuilder()
                    .solicitudeId(100)
                    .loanType(LoanType.builder().loanTypeId(input.getLoanType().getLoanTypeId()).build())
                    .state(State.builder().stateId(input.getState().getStateId()).build())
                    .build()
            );
        });
        when(userPort.getUserByEmail(anyString())).thenReturn(Mono.just(mockUser));
        when(solicitudeRepository.findTotalMonthlyFee(anyString())).thenReturn(Mono.just(BigDecimal.ZERO));
        when(jwtPort.generateCallbackToken(anyString())).thenReturn("fake-callback-token");
        when(sqsPort.sendDebtCapacity(any())).thenReturn(Mono.empty());

        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345", testJwtData))
                .expectNextMatches(result ->
                        result.getSolicitudeId() == 100 &&
                                result.getEmail().equals(testJwtData.subject()) &&
                                result.getLoanType() != null && result.getLoanType().getLoanTypeId() == 1 &&
                                result.getState() != null && result.getState().getStateId() == 1
                )
                .verifyComplete();
        verify(sqsPort).sendDebtCapacity(any());
    }

    @Test
    void shouldSaveSolicitudeAndSkipPostProcessWhenAutoValidationIsFalse() {
        // Arrange
        LoanType loanTypeNoAutoValidation = testLoanType.toBuilder().autoValidation(false).build();
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(loanTypeNoAutoValidation));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(pendingState));
        when(solicitudeRepository.save(any(Solicitude.class))).thenAnswer(invocation -> {
            Solicitude input = invocation.getArgument(0);
            return Mono.just(input.toBuilder().solicitudeId(101).build());
        });

        // Act & Assert
        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345", testJwtData))
                .expectNextMatches(result -> result.getSolicitudeId() == 101)
                .verifyComplete();

        // Verify that post-processing methods were NOT called
        verify(userPort, never()).getUserByEmail(anyString());
        verify(sqsPort, never()).sendDebtCapacity(any());
    }

    @Test
    void shouldSaveSolicitudeAndContinueWhenPostProcessFails() {
        // Arrange: Setup for a successful save, but a failing post-process
        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType)); // autoValidation is true
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(pendingState));
        when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.just(testSolicitude.toBuilder().solicitudeId(102).build()));

        // Act & Assert: The main flow should still complete successfully due to onErrorResume
        StepVerifier.create(solicitudeUseCase.saveSolicitude(testSolicitude, "12345", testJwtData))
                .expectNextMatches(result -> result.getSolicitudeId() == 102)
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
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(pendingState));

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
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(pendingState));

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
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(pendingState));

        StepVerifier.create(solicitudeUseCase.saveSolicitude(invalidSolicitude, "12345", testJwtData))
                .expectError()
                .verify();
    }

    @Test
    void shouldFailWhenRepositorySaveFails() {
        RuntimeException dbException = new RuntimeException("Database connection failed");

        when(loanTypeRepository.findById(anyInt())).thenReturn(Mono.just(testLoanType));
        when(stateRepository.findOne(any(State.class))).thenReturn(Mono.just(pendingState));
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

    @Nested
    class ApproveRejectSolicitudeStateTests {

        @BeforeEach
        void nestedSetUp() {
            // Set up the test solicitude with a pending state for these tests
            testSolicitude = testSolicitude.toBuilder()
                    .solicitudeId(1)
                    .email("test@example.com")
                    .state(pendingState)
                    .build();
        }

        @Test
        void approveRejectSolicitudeState_happyPath() {
            // Arrange
            Solicitude updatedSolicitude = testSolicitude.toBuilder().state(approvedState).build();

            when(stateRepository.findByName("APROBADO")).thenReturn(Mono.just(approvedState));
            when(solicitudeRepository.findById(1)).thenReturn(Mono.just(testSolicitude));
            when(loanTypeRepository.findById(any())).thenReturn(Mono.just(testLoanType));
            when(stateRepository.findOne(any())).thenReturn(Mono.just(pendingState));
            when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.just(updatedSolicitude));
            when(templatePort.process(any(), any())).thenReturn(Mono.just("<html>Email Body</html>"));
            when(sqsPort.sendEmail(any(EmailMessage.class))).thenReturn(Mono.empty());
            when(sqsPort.sendMetric(any())).thenReturn(Mono.empty()); // Add mock for sendMetric

            // Act
            Mono<Solicitude> result = solicitudeUseCase.approveRejectSolicitudeState(1, "APROBADO");

            StepVerifier.create(result)
                    .expectNextMatches(solicitude -> solicitude.getState().getName().equals("APROBADO"))
                    .verifyComplete();
            verify(solicitudeRepository).save(any(Solicitude.class));
            verify(templatePort).process(any(), any());
            verify(sqsPort).sendEmail(any(EmailMessage.class));
        }

        @Test
        void approveRejectSolicitudeState_shouldSucceedEvenIfEmailFails() {
            // Arrange
            when(stateRepository.findByName("APROBADO")).thenReturn(Mono.just(approvedState));
            when(solicitudeRepository.findById(1)).thenReturn(Mono.just(testSolicitude));
            when(loanTypeRepository.findById(any())).thenReturn(Mono.just(testLoanType));
            when(stateRepository.findOne(any())).thenReturn(Mono.just(pendingState));
            when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.just(testSolicitude.toBuilder().state(approvedState).build()));
            when(templatePort.process(any(), any())).thenReturn(Mono.just("<html>Email Body</html>"));
            when(sqsPort.sendEmail(any(EmailMessage.class))).thenReturn(Mono.error(new RuntimeException("SQS is down"))); // Simulate email failure
            when(sqsPort.sendMetric(any())).thenReturn(Mono.empty()); // Add mock for sendMetric

            // Act
            Mono<Solicitude> result = solicitudeUseCase.approveRejectSolicitudeState(1, "APROBADO");

            // Assert: The main flow should still complete successfully
            StepVerifier.create(result)
                    .expectNextMatches(solicitude -> solicitude.getState().getName().equals("APROBADO"))
                    .verifyComplete();
        }

        @Test
        void approveRejectSolicitudeState_invalidStateName() {

            // Act
            Mono<Solicitude> result = solicitudeUseCase.approveRejectSolicitudeState(1, "INVALID_STATE");

            // Assert
            StepVerifier.create(result)
                    .expectError(InvalidFieldException.class)
                    .verify();

            verify(solicitudeRepository, never()).findById(anyInt());
            verify(sqsPort, never()).sendEmail(any(EmailMessage.class));
        }

        @Test
        void approveRejectSolicitudeState_stateNotFoundInDb() {
            // Arrange
            when(stateRepository.findByName("APROBADO")).thenReturn(Mono.empty());
            when(solicitudeRepository.findById(anyInt())).thenReturn(Mono.just(testSolicitude));

            // Act
            Mono<Solicitude> result = solicitudeUseCase.approveRejectSolicitudeState(1, "APROBADO");

            // Assert
            StepVerifier.create(result)
                    .expectError(StateNotFoundException.class)
                    .verify();
        }

        @Test
        void approveRejectSolicitudeState_solicitudeNotFound() {
            // Arrange
            when(stateRepository.findByName("APROBADO")).thenReturn(Mono.just(approvedState));
            when(solicitudeRepository.findById(999)).thenReturn(Mono.empty());

            // Act
            Mono<Solicitude> result = solicitudeUseCase.approveRejectSolicitudeState(999, "APROBADO");

            // Assert
            StepVerifier.create(result)
                    .expectError(SolicitudeNullException.class)
                    .verify();

            verify(solicitudeRepository, never()).save(any());
            verify(sqsPort, never()).sendEmail(any(EmailMessage.class));
        }


        @Test
        void approveRejectSolicitudeState_shouldFailWhenStateIsUnchanged() {
            // Arrange
            // The testSolicitude is already in PENDING state. We try to change it to PENDING again.
            String sameState = DefaultValues.APPROVED_STATE;

            // Mocks for changeState
            when(stateRepository.findByName(sameState)).thenReturn(Mono.just(pendingState));
            when(solicitudeRepository.findById(1)).thenReturn(Mono.just(testSolicitude));

            // Mocks for the internal 'completeSolicitude' call within saveStateChange
            when(loanTypeRepository.findById(any())).thenReturn(Mono.just(testLoanType));
            when(stateRepository.findOne(any())).thenReturn(Mono.just(pendingState));

            // Act
            Mono<Solicitude> result = solicitudeUseCase.approveRejectSolicitudeState(1, sameState);

            // Assert
            StepVerifier.create(result)
                    .expectError(InvalidFieldException.class)
                    .verify();

            // Verify that save was never called because the flow errored out before.
            verify(solicitudeRepository, never()).save(any(Solicitude.class));
        }
    }

    @Nested
    class DebtCapacityStateChangeTests {

        @BeforeEach
        void nestedSetUp() {
            testSolicitude = Solicitude.builder()
                    .solicitudeId(1)
                    .email("test@example.com")
                    .state(pendingState)
                    .loanType(testLoanType)
                    .value(new BigDecimal("5000"))
                    .deadline(12)
                    .build();
        }

        @Test
        void debtCapacityStateChange_withApprovedState_shouldSendPayPlanEmail() {
            // Arrange
            Solicitude updatedSolicitude = testSolicitude.toBuilder().state(approvedState).build();

            when(stateRepository.findByName("APROBADO")).thenReturn(Mono.just(approvedState));
            when(solicitudeRepository.findById(1)).thenReturn(Mono.just(testSolicitude));
            when(loanTypeRepository.findById(any())).thenReturn(Mono.just(testLoanType));
            when(stateRepository.findOne(any())).thenReturn(Mono.just(pendingState));
            when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.just(updatedSolicitude));
            when(templatePort.process(any(), any())).thenReturn(Mono.just("<html>Pay Plan</html>"));
            when(sqsPort.sendMetric(any())).thenReturn(Mono.empty()); // Add mock for sendMetric

            // Act
            Mono<EmailMessage> result = solicitudeUseCase.debtCapacityStateChange(1, "APROBADO");

            // Assert
            StepVerifier.create(result)
                    .expectNextMatches(emailMessage ->
                            emailMessage.getSubject().equals("Loan application pay plan") &&
                                    emailMessage.getBody().equals("<html>Pay Plan</html>")
                    )
                    .verifyComplete();
        }

        @Test
        void debtCapacityStateChange_withRejectedState_shouldSendStateChangeEmail() {
            // Arrange
            Solicitude updatedSolicitude = testSolicitude.toBuilder().state(rejectedState).build();

            when(stateRepository.findByName("RECHAZADO")).thenReturn(Mono.just(rejectedState));
            when(solicitudeRepository.findById(1)).thenReturn(Mono.just(testSolicitude));
            when(loanTypeRepository.findById(any())).thenReturn(Mono.just(testLoanType));
            when(stateRepository.findOne(any())).thenReturn(Mono.just(pendingState));
            when(solicitudeRepository.save(any(Solicitude.class))).thenReturn(Mono.just(updatedSolicitude));
            when(templatePort.process(any(), any())).thenReturn(Mono.just("<html>State Change</html>"));

            // Act
            Mono<EmailMessage> result = solicitudeUseCase.debtCapacityStateChange(1, "RECHAZADO");

            // Assert
            StepVerifier.create(result)
                    .expectNextMatches(emailMessage ->
                            emailMessage.getSubject().equals(NotificationUtils.DEFAULT_STATE_CHANGE) &&
                                    emailMessage.getBody().equals("<html>State Change</html>") &&
                                    emailMessage.getTo().equals(testSolicitude.getEmail())
                    )
                    .verifyComplete();
        }

        @Test
        void debtCapacityStateChange_whenChangeStateFails() {
            // Arrange
            when(stateRepository.findByName(anyString())).thenReturn(Mono.just(approvedState));
            when(solicitudeRepository.findById(anyInt())).thenReturn(Mono.error(new SolicitudeNullException()));

            // Act & Assert
            StepVerifier.create(solicitudeUseCase.debtCapacityStateChange(1, "APROBADO"))
                    .expectError(SolicitudeNullException.class)
                    .verify();
        }

    }
}