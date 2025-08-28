package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class StateNotFoundException extends SolicitudeException {
    public StateNotFoundException()  {
        super(Errors.LOAN_TYPE_NOT_FOUND, Errors.LOAN_TYPE_NOT_FOUND_CODE);
    }
}
