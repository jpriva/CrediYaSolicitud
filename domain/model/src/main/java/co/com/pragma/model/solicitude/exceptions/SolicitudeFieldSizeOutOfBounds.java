package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class SolicitudeFieldSizeOutOfBounds extends SolicitudeException {
    public final String field;
    public SolicitudeFieldSizeOutOfBounds(String field) {
        super(field + " " +Errors.SIZE_OUT_OF_BOUNDS, Errors.SIZE_OUT_OF_BOUNDS_CODE);
        this.field = field;
    }
}
