package co.com.pragma.api.config;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CallbackTokenAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtProviderPort jwtProviderPort;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return jwtProviderPort.validateCallbackToken(authToken)
                .filter(isValid -> isValid)
                .flatMap(valid -> Mono.fromCallable(() -> jwtProviderPort.getClaims(authToken)))
                .filter(data -> ApiConstants.Role.CALLBACK_ROLE.equals(data.role()))
                .map(data -> new UsernamePasswordAuthenticationToken(
                        data.subject(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(ApiConstants.Role.CALLBACK_ROLE))
                ));
    }
}
