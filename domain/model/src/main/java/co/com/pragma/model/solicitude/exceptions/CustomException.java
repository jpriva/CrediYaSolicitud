package co.com.pragma.model.solicitude.exceptions;

import lombok.Getter;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

@Getter
public class CustomException extends RuntimeException {
    private final String code;
    private final int webStatus;
    public CustomException(String message, String code) {
        super(message);
        this.code = code;
        this.webStatus = HTTP_BAD_REQUEST;
    }
    public CustomException(String message, String code, int webStatus) {
        super(message);
        this.code = code;
        this.webStatus = webStatus;
    }
}
