package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.solicitude.exceptions.CustomException;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String message, String code) {
        super(message, code);
    }
}