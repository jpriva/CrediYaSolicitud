package co.com.pragma.model.exceptions;

import static co.com.pragma.model.constants.Errors.USER_NOT_FOUND;
import static co.com.pragma.model.constants.Errors.USER_NOT_FOUND_CODE;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String email) {
        super(USER_NOT_FOUND + " Email: " + email, USER_NOT_FOUND_CODE);
    }
}