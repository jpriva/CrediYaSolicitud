package co.com.pragma.model.solicitude.exceptions;

import co.com.pragma.model.constants.Errors;

public class FieldBlankException extends CustomException {

    public final String field;
    public FieldBlankException(String field) {
        super(field + " " + Errors.REQUIRED_FIELDS, Errors.REQUIRED_FIELDS_CODE);
        this.field = field;
    }
}
