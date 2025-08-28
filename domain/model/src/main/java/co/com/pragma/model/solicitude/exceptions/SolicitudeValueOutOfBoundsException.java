package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class SolicitudeValueOutOfBoundsException extends SolicitudeException {

    public SolicitudeValueOutOfBoundsException(String message) {
        super(message, Errors.VALUE_OUT_OF_BOUNDS_CODE);
    }
}
