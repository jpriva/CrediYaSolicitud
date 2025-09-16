package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.state.State;
import co.com.pragma.model.template.Installment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationUtilsTest {

    private String formatCurrencyForTest(BigDecimal value) {
        if (value == null) return "N/A";
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return currencyFormatter.format(value);
    }

    @Nested
    @DisplayName("solicitudeChangeStateBody Tests")
    class SolicitudeChangeStateBodyTests {

        @Test
        @DisplayName("Should create context map for state change notification")
        void shouldCreateContextMapForStateChange() {
            // Arrange
            LoanType loanType = LoanType.builder().name("Personal Loan").build();
            State state = State.builder().name(DefaultValues.APPROVED_STATE).description("Your loan is approved").build();
            Solicitude solicitude = Solicitude.builder()
                    .solicitudeId(123)
                    .loanType(loanType)
                    .value(new BigDecimal("10000"))
                    .state(state)
                    .build();

            // Act
            Map<String, Object> context = NotificationUtils.solicitudeChangeStateBody(solicitude);

            // Assert
            assertAll(
                    () -> assertEquals(123, context.get("solicitudeId")),
                    () -> assertEquals("Personal Loan", context.get("loanType")),
                    () -> assertEquals(formatCurrencyForTest(new BigDecimal("10000")), context.get("value")),
                    () -> assertEquals(DefaultValues.APPROVED_STATE, context.get("stateName")),
                    () -> assertEquals("Your loan is approved", context.get("stateDescription")),
                    () -> assertEquals("approved", context.get("stateClass"))
            );
        }

        @ParameterizedTest
        @CsvSource({
                "APROBADO, approved",
                "RECHAZADO, rejected",
                "MANUAL, manual",
                "PENDIENTE, pending",
                "SOME_OTHER_STATE, pending"
        })
        @DisplayName("Should return correct state class for different state names")
        void shouldReturnCorrectStateClass(String stateName, String expectedClass) {
            // Arrange
            State state = State.builder().name(stateName).build();
            Solicitude solicitude = Solicitude.builder()
                    .solicitudeId(1)
                    .loanType(LoanType.builder().name("Test").build())
                    .value(BigDecimal.ONE)
                    .state(state)
                    .build();

            // Act
            Map<String, Object> context = NotificationUtils.solicitudeChangeStateBody(solicitude);

            // Assert
            assertEquals(expectedClass, context.get("stateClass"));
        }
    }

    @Nested
    @DisplayName("userPayPlan Tests")
    class UserPayPlanTests {

        @Test
        @DisplayName("Should generate correct pay plan for a standard loan")
        void shouldGenerateCorrectPayPlan() {
            // Arrange
            LoanType loanType = LoanType.builder().interestRate(new BigDecimal("2")).build();
            Solicitude solicitude = Solicitude.builder()
                    .solicitudeId(456)
                    .value(new BigDecimal("1000"))
                    .deadline(3)
                    .loanType(loanType)
                    .build();

            // Act
            Map<String, Object> context = NotificationUtils.userPayPlan(solicitude);

            // Assert
            assertEquals(456, context.get("solicitudeId"));
            assertEquals(formatCurrencyForTest(new BigDecimal("1000")), context.get("loanValue"));
            assertEquals("2.00%", context.get("interestRate"));
            assertEquals(3, context.get("deadline"));
            assertEquals(formatCurrencyForTest(new BigDecimal("346.75")), context.get("monthlyPayment"));

            @SuppressWarnings("unchecked")
            List<Installment> installments = (List<Installment>) context.get("installments");
            assertNotNull(installments);
            assertEquals(3, installments.size());

            // Check first installment
            Installment first = installments.get(0);
            assertEquals(1, first.month());
            assertEquals(formatCurrencyForTest(new BigDecimal("326.75")), first.principal()); // 346.75 - 20 (2% of 1000)
            assertEquals(formatCurrencyForTest(new BigDecimal("20.00")), first.interest());
            assertEquals(formatCurrencyForTest(new BigDecimal("673.25")), first.remainingBalance()); // 1000 - 326.75

            // Check last installment
            Installment last = installments.get(2);
            assertEquals(3, last.month());
            assertEquals(formatCurrencyForTest(BigDecimal.ZERO), last.remainingBalance());
        }

        @Test
        @DisplayName("Should generate correct pay plan for a zero interest loan")
        void shouldGenerateCorrectPayPlanForZeroInterest() {
            // Arrange
            LoanType loanType = LoanType.builder().interestRate(BigDecimal.ZERO).build();
            Solicitude solicitude = Solicitude.builder()
                    .solicitudeId(789)
                    .value(new BigDecimal("1200"))
                    .deadline(4)
                    .loanType(loanType)
                    .build();

            // Act
            Map<String, Object> context = NotificationUtils.userPayPlan(solicitude);

            // Assert
            assertEquals(formatCurrencyForTest(new BigDecimal("300")), context.get("monthlyPayment"));
            assertEquals(formatCurrencyForTest(BigDecimal.ZERO), context.get("totalInterest"));
        }

        @Test
        @DisplayName("Should throw NullPointerException for missing required data")
        void shouldThrowExceptionForMissingData() {
            assertThrows(NullPointerException.class, () -> NotificationUtils.userPayPlan(null));

            Solicitude s1 = Solicitude.builder().value(null).build();
            assertThrows(NullPointerException.class, () -> NotificationUtils.userPayPlan(s1));

            Solicitude s2 = Solicitude.builder().value(BigDecimal.ONE).loanType(null).build();
            assertThrows(NullPointerException.class, () -> NotificationUtils.userPayPlan(s2));

            Solicitude s3 = Solicitude.builder().value(BigDecimal.ONE).loanType(LoanType.builder().interestRate(null).build()).build();
            assertThrows(NullPointerException.class, () -> NotificationUtils.userPayPlan(s3));
        }
    }
}