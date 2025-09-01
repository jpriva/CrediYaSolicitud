package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class InvalidFieldException extends CustomException {
    public final String field;
    public InvalidFieldException(String field) {
        super(field + " " + Errors.INVALID_FIELD, Errors.INVALID_FIELD_CODE);
        this.field = field;
    }
}
