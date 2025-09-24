package co.com.pragma.api.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CallbackTokenAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CALLBACK_HEADER = "X-Callback-Token";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(CALLBACK_HEADER))
                .filter(token -> !token.isBlank())
                .map(header -> header.substring(BEARER_PREFIX.length()))
                .map(token -> new UsernamePasswordAuthenticationToken(token, token));
    }
}
