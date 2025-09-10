package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeValueErrorException;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.exceptions.FieldBlankException;
import co.com.pragma.model.exceptions.FieldSizeOutOfBounds;
import co.com.pragma.model.exceptions.ValueOutOfBoundsException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolicitudeUtilsTest {

    @Nested
    class TrimTests {

        @Test
        void shouldTrimEmailAndScaleValue() {
            Solicitude solicitude = Solicitude.builder()
                    .email("  test@example.com  ")
                    .value(new BigDecimal("123.456"))
                    .build();

            StepVerifier.create(SolicitudeUtils.trim(solicitude))
                    .assertNext(trimmed -> {
                        assertEquals("test@example.com", trimmed.getEmail());
                        assertEquals(new BigDecimal("123.46"), trimmed.getValue());
                    })
                    .verifyComplete();
        }

        @Test
        void shouldHandleNullEmail() {
            Solicitude solicitude = Solicitude.builder().email(null).build();
            StepVerifier.create(SolicitudeUtils.trim(solicitude))
                    .assertNext(trimmed -> assertNull(trimmed.getEmail()))
                    .verifyComplete();
        }

        @Test
        void shouldHandleNullValue() {
            Solicitude solicitude = Solicitude.builder().value(null).build();
            StepVerifier.create(SolicitudeUtils.trim(solicitude))
                    .assertNext(trimmed -> assertNull(trimmed.getValue()))
                    .verifyComplete();
        }

        @Test
        void shouldHandleAllNulls() {
            Solicitude solicitude = Solicitude.builder().build();
            StepVerifier.create(SolicitudeUtils.trim(solicitude))
                    .assertNext(trimmed -> {
                        assertNull(trimmed.getEmail());
                        assertNull(trimmed.getValue());
                    })
                    .verifyComplete();
        }
    }

    @Nested
    class ValidateFieldsTests {

        private Solicitude createValidSolicitude() {
            return Solicitude.builder()
                    .value(BigDecimal.valueOf(10000))
                    .deadline(12)
                    .email("test@example.com")
                    .loanType(LoanType.builder().loanTypeId(1).build())
                    .build();
        }

        @Test
        void shouldPassValidationForValidSolicitude() {
            Solicitude validSolicitude = createValidSolicitude();
            StepVerifier.create(SolicitudeUtils.validateFields(validSolicitude))
                    .expectNext(validSolicitude)
                    .verifyComplete();
        }

        @Test
        void shouldFailWhenValueIsNull() {
            Solicitude solicitude = createValidSolicitude().toBuilder().value(null).build();
            StepVerifier.create(SolicitudeUtils.validateFields(solicitude))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void shouldFailWhenDeadlineIsNull() {
            Solicitude solicitude = createValidSolicitude().toBuilder().deadline(null).build();
            StepVerifier.create(SolicitudeUtils.validateFields(solicitude))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void shouldFailWhenEmailIsBlank() {
            Solicitude solicitude = createValidSolicitude().toBuilder().email("  ").build();
            StepVerifier.create(SolicitudeUtils.validateFields(solicitude))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void shouldFailWhenLoanTypeIdIsNull() {
            Solicitude solicitude = createValidSolicitude().toBuilder()
                    .loanType(LoanType.builder().loanTypeId(null).build())
                    .build();
            StepVerifier.create(SolicitudeUtils.validateFields(solicitude))
                    .expectError(FieldBlankException.class)
                    .verify();
        }

        @Test
        void shouldFailWhenDeadlineIsOutOfBounds() {
            Solicitude solicitude = createValidSolicitude().toBuilder().deadline(0).build();
            StepVerifier.create(SolicitudeUtils.validateFields(solicitude))
                    .expectError(FieldSizeOutOfBounds.class)
                    .verify();
        }

        @Test
        void shouldFailWhenEmailIsTooLong() {
            String longEmail = "a".repeat(DefaultValues.MAX_LENGTH_EMAIL + 1) + "@example.com";
            Solicitude solicitude = createValidSolicitude().toBuilder().email(longEmail).build();
            StepVerifier.create(SolicitudeUtils.validateFields(solicitude))
                    .expectError(FieldSizeOutOfBounds.class)
                    .verify();
        }
    }

    @Nested
    class VerifySolicitudeLoanTypeTests {

        private LoanType createValidLoanType() {
            return LoanType.builder()
                    .minValue(new BigDecimal("5000.00"))
                    .maxValue(new BigDecimal("50000.00"))
                    .build();
        }

        private Solicitude createSolicitudeWithValue(String value) {
            return Solicitude.builder()
                    .value(new BigDecimal(value))
                    .build();
        }

        @Test
        void shouldPassWhenValueIsWithinRange() {
            Solicitude solicitude = createSolicitudeWithValue("10000.00");
            LoanType loanType = createValidLoanType();
            solicitude = solicitude.toBuilder().loanType(loanType).build();

            StepVerifier.create(SolicitudeUtils.verifySolicitudeLoanType(solicitude))
                    .expectNext(solicitude)
                    .verifyComplete();
        }

        @Test
        void shouldPassWhenValueIsExactlyMinValue() {
            Solicitude solicitude = createSolicitudeWithValue("5000.00");
            LoanType loanType = createValidLoanType();
            solicitude = solicitude.toBuilder().loanType(loanType).build();

            StepVerifier.create(SolicitudeUtils.verifySolicitudeLoanType(solicitude))
                    .expectNext(solicitude)
                    .verifyComplete();
        }

        @Test
        void shouldFailWhenValueIsBelowMin() {
            Solicitude solicitude = createSolicitudeWithValue("4999.99");
            LoanType loanType = createValidLoanType();
            solicitude = solicitude.toBuilder().loanType(loanType).build();

            StepVerifier.create(SolicitudeUtils.verifySolicitudeLoanType(solicitude))
                    .expectError(ValueOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void shouldFailWhenValueIsAboveMax() {
            Solicitude solicitude = createSolicitudeWithValue("50000.01");
            LoanType loanType = createValidLoanType();
            solicitude = solicitude.toBuilder().loanType(loanType).build();

            StepVerifier.create(SolicitudeUtils.verifySolicitudeLoanType(solicitude))
                    .expectError(ValueOutOfBoundsException.class)
                    .verify();
        }

        @Test
        void shouldFailWhenLoanTypeMinValueIsNull() {
            Solicitude solicitude = createSolicitudeWithValue("10000.00");
            LoanType loanType = createValidLoanType().toBuilder().minValue(null).build();
            solicitude = solicitude.toBuilder().loanType(loanType).build();

            StepVerifier.create(SolicitudeUtils.verifySolicitudeLoanType(solicitude))
                    .expectError(LoanTypeValueErrorException.class)
                    .verify();
        }

        @Test
        void shouldFailWhenLoanTypeMaxValueIsNegative() {
            Solicitude solicitude = createSolicitudeWithValue("10000.00");
            LoanType loanType = createValidLoanType().toBuilder().maxValue(new BigDecimal("-100")).build();
            solicitude = solicitude.toBuilder().loanType(loanType).build();

            StepVerifier.create(SolicitudeUtils.verifySolicitudeLoanType(solicitude))
                    .expectError(LoanTypeValueErrorException.class)
                    .verify();
        }
    }
}