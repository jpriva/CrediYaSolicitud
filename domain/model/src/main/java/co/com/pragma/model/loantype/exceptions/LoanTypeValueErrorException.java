package co.com.pragma.model.loantype.exceptions;

import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.exceptions.CustomException;

public class LoanTypeValueErrorException extends CustomException {
    public LoanTypeValueErrorException() {
        super(Errors.LOAN_TYPE_MIN_MAX_VALUE_ERROR, Errors.LOAN_TYPE_MIN_MAX_VALUE_ERROR_CODE);
    }
}
