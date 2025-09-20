package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.Errors;

public class FieldSizeOutOfBounds extends CustomException {
    public final String field;
    public FieldSizeOutOfBounds(String field) {
        super(field + " " +Errors.SIZE_OUT_OF_BOUNDS, Errors.SIZE_OUT_OF_BOUNDS_CODE);
        this.field = field;
    }
}
