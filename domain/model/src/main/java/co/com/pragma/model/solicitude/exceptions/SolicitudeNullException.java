package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.exceptions.CustomException;

public class SolicitudeNullException extends CustomException {
    public SolicitudeNullException() {
        super(Errors.SOLICITUDE_NULL, Errors.SOLICITUDE_NULL_CODE);
    }
}
