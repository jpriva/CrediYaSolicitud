package co.com.pragma.model.user.exceptions;

import co.com.pragma.model.solicitude.exceptions.CustomException;

public class UserGatewayException extends CustomException {
    public UserGatewayException(String message, String code) {
        super(message, code);
    }
}