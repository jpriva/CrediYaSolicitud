package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.exceptions.SolicitudeException;
import co.com.pragma.model.solicitude.exceptions.SolicitudeFieldBlankException;
import co.com.pragma.model.solicitude.exceptions.SolicitudeFieldSizeOutOfBounds;

public class SolicitudeUtils {

    private SolicitudeUtils() {
    }

    public static SolicitudeException validateFields(Solicitude solicitude) {
        SolicitudeException exception = validateRequiredFields(solicitude);
        if (exception != null) {
            return exception;
        }
        exception = validateFieldsBounds(solicitude);
        return exception;
    }

    private static SolicitudeException validateRequiredFields(Solicitude solicitude) {
        if (solicitude.getValue() == null) {
            return new SolicitudeFieldBlankException(DefaultValues.VALUE_FIELD);
        }
        if (solicitude.getTerm() == null) {
            return new SolicitudeFieldBlankException(DefaultValues.TERM_FIELD);
        }
        if (solicitude.getEmail() == null || solicitude.getEmail().isBlank()) {
            return new SolicitudeFieldBlankException(DefaultValues.EMAIL_FIELD);
        }
        if (solicitude.getLoanType() == null || solicitude.getLoanType().getLoanTypeId() == null) {
            return new SolicitudeFieldBlankException(DefaultValues.LOAN_TYPE_FIELD);
        }
        return null;
    }

    private static SolicitudeException validateFieldsBounds(Solicitude solicitude) {
        if (solicitude.getTerm() < DefaultValues.MIN_LENGTH_TERM || solicitude.getTerm() > DefaultValues.MAX_LENGTH_TERM) {
            return new SolicitudeFieldSizeOutOfBounds(DefaultValues.TERM_FIELD);
        }
        if (solicitude.getLoanType().getLoanTypeId() < DefaultValues.MIN_LOAN_TYPE) {
            return new SolicitudeFieldSizeOutOfBounds(DefaultValues.LOAN_TYPE_FIELD);
        }
        if (solicitude.getEmail().length() > DefaultValues.MAX_LENGTH_TERM) {
            return new SolicitudeFieldSizeOutOfBounds(DefaultValues.EMAIL_FIELD);
        }
        return null;
    }
}
