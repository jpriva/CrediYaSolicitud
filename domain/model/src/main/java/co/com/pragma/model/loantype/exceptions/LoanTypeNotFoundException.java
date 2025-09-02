package co.com.pragma.model.loantype.exceptions;

import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.exceptions.CustomException;

public class LoanTypeNotFoundException extends CustomException {
    public LoanTypeNotFoundException() {
        super(Errors.LOAN_TYPE_NOT_FOUND, Errors.LOAN_TYPE_NOT_FOUND_CODE);
    }
}
