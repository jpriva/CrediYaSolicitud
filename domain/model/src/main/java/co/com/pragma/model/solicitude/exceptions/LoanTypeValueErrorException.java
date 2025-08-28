package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class LoanTypeValueErrorException extends SolicitudeException {
    public LoanTypeValueErrorException() {
        super(Errors.LOAN_TYPE_MIN_MAX_VALUE_ERROR, Errors.LOAN_TYPE_MIN_MAX_VALUE_ERROR_CODE);
    }
}
