package co.com.pragma.model.jwt.gateways;

import co.com.pragma.model.jwt.JwtData;

public interface JwtProviderPort {

    JwtData getClaims(String token);
}
