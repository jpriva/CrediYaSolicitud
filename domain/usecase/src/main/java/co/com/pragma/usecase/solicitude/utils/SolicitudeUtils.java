package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.exceptions.LoanTypeValueErrorException;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.FieldBlankException;
import co.com.pragma.model.solicitude.exceptions.FieldSizeOutOfBounds;
import co.com.pragma.model.solicitude.exceptions.ValueOutOfBoundsException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringJoiner;

import static co.com.pragma.model.constants.DefaultValues.DECIMAL_FORMAT;
import static co.com.pragma.usecase.utils.ValidationUtils.validateCondition;

public class SolicitudeUtils {

    private SolicitudeUtils() {
    }

    public static Mono<Solicitude> trim(Solicitude solicitude) {
        return Mono.fromCallable(() -> solicitude.toBuilder()
                .email(solicitude.getEmail() != null ? solicitude.getEmail().trim() : null)
                .value(solicitude.getValue() != null ? solicitude.getValue().setScale(2, RoundingMode.HALF_UP) : null)
                .build());
    }

    public static Mono<Solicitude> validateFields(Solicitude solicitude) {
        return validateRequiredFields(solicitude)
                .then(Mono.defer(() -> validateFieldsBounds(solicitude)))
                .thenReturn(solicitude);
    }

    private static Mono<Void> validateRequiredFields(Solicitude solicitude) {
        return validateCondition(solicitude.getValue() != null,
                () -> new FieldBlankException(DefaultValues.VALUE_FIELD))
                .then(validateCondition(solicitude.getDeadline() != null,
                        () -> new FieldBlankException(DefaultValues.DEADLINE_FIELD)))
                .then(validateCondition(solicitude.getEmail() != null &&
                                !solicitude.getEmail().isBlank(),
                        () -> new FieldBlankException(DefaultValues.EMAIL_FIELD)))
                .then(validateCondition(solicitude.getLoanType() != null &&
                                solicitude.getLoanType().getLoanTypeId() != null,
                        () -> new FieldBlankException(DefaultValues.LOAN_TYPE_FIELD)));
    }

    private static Mono<Void> validateFieldsBounds(Solicitude solicitude) {
        return validateCondition(solicitude.getDeadline() >= DefaultValues.MIN_LENGTH_DEADLINE &&
                        solicitude.getDeadline() <= DefaultValues.MAX_LENGTH_DEADLINE,
                () -> new FieldSizeOutOfBounds(DefaultValues.DEADLINE_FIELD))
                .then(validateCondition(solicitude.getLoanType().getLoanTypeId() >= DefaultValues.MIN_LOAN_TYPE,
                        () -> new FieldSizeOutOfBounds(DefaultValues.LOAN_TYPE_FIELD)))
                .then(validateCondition(solicitude.getEmail().length() <= DefaultValues.MAX_LENGTH_EMAIL,
                        () -> new FieldSizeOutOfBounds(DefaultValues.EMAIL_FIELD)));
    }

    public static Mono<Solicitude> verifySolicitudeLoanType(Solicitude solicitude, LoanType loanType) {
        return validateCondition(loanType.getMinValue() != null &&
                        loanType.getMinValue().compareTo(BigDecimal.ZERO) >= 0 &&
                        loanType.getMaxValue() != null &&
                        loanType.getMaxValue().compareTo(BigDecimal.ZERO) >= 0,
                LoanTypeValueErrorException::new)
                .then(Mono.defer(() -> validateCondition(solicitude.getValue().compareTo(loanType.getMinValue()) >= 0 &&
                                solicitude.getValue().compareTo(loanType.getMaxValue()) <= 0,
                        () -> new ValueOutOfBoundsException(loanTypeRangeMessage(loanType.getMinValue(), loanType.getMaxValue())))))
                .thenReturn(solicitude);
    }

    private static String loanTypeRangeMessage(BigDecimal minValue, BigDecimal maxValue) {
        StringJoiner message = new StringJoiner(" ")
                .add(DefaultValues.VALUE_FIELD)
                .add(DefaultValues.MOST_BE_BETWEEN)
                .add(DECIMAL_FORMAT.format(minValue))
                .add(DefaultValues.AND_CONNECTOR)
                .add(DECIMAL_FORMAT.format(maxValue));
        return message.toString();
    }
}
