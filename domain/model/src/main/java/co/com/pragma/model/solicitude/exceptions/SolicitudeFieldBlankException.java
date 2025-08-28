package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class SolicitudeFieldBlankException extends SolicitudeException {

    public final String field;
    public SolicitudeFieldBlankException(String field) {
        super(field + " " + Errors.REQUIRED_FIELDS, Errors.REQUIRED_FIELDS_CODE);
        this.field = field;
    }
}
