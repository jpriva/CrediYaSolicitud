package co.com.pragma.model.jwt.gateways;

import co.com.pragma.model.jwt.JwtData;

public interface JwtProviderPort {

    String generateCallbackToken(String taskId);
    JwtData getClaims(String token);
}
