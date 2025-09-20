package co.com.pragma.model.exceptions;

public class UserGatewayException extends CustomException {
    public UserGatewayException(String message, String code) {
        super(message, code);
    }

    public UserGatewayException(String message, String code, Throwable cause) {
        super(message, code, cause);
    }

}