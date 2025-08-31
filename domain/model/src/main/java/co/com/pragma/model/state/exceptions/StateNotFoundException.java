package co.com.pragma.model.state.exceptions;

import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.solicitude.exceptions.CustomException;

public class StateNotFoundException extends CustomException {
    public StateNotFoundException()  {
        super(Errors.LOAN_TYPE_NOT_FOUND, Errors.LOAN_TYPE_NOT_FOUND_CODE);
    }
}
