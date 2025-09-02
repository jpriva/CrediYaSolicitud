package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.Errors;

import java.net.HttpURLConnection;

public class InvalidCredentialsException extends CustomException {

    public InvalidCredentialsException() {
        super(Errors.INVALID_CREDENTIALS, Errors.INVALID_CREDENTIALS_CODE, HttpURLConnection.HTTP_UNAUTHORIZED);
    }
}
