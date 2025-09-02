package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.Errors;

public class ValueOutOfBoundsException extends CustomException {

    public ValueOutOfBoundsException(String message) {
        super(message, Errors.VALUE_OUT_OF_BOUNDS_CODE);
    }
}
