package co.com.pragma.model.exceptions;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String message, String code) {
        super(message, code);
    }
}