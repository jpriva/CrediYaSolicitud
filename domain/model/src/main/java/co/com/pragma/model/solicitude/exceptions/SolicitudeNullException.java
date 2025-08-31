package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class SolicitudeNullException extends CustomException {
    public SolicitudeNullException() {
        super(Errors.SOLICITUDE_NULL, Errors.SOLICITUDE_NULL_CODE);
    }
}
