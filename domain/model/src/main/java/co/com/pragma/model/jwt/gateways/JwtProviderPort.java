package co.com.pragma.model.jwt.gateways;

import co.com.pragma.model.jwt.JwtData;
import reactor.core.publisher.Mono;

public interface JwtProviderPort {

    String generateCallbackToken(String taskId);
    JwtData getClaims(String token);
    Mono<Boolean> validateCallbackToken(String token);
}
