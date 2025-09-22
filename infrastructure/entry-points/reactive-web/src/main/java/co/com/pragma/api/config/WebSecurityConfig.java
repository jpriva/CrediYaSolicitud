package co.com.pragma.api.config;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.constants.ApiConstants.ApiPathMatchers;
import co.com.pragma.api.exception.handler.CustomAccessDeniedHandler;
import co.com.pragma.api.exception.handler.CustomAuthenticationEntryPoint;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.jwt.JwtData;
import co.com.pragma.model.jwt.gateways.JwtProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtProviderPort jwtProvider;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(new JwtSecurityContextRepository())
                .authorizeExchange(spec -> spec
                        .pathMatchers(
                                ApiPathMatchers.API_DOCS_MATCHER,
                                ApiPathMatchers.SWAGGER_UI_MATCHER,
                                ApiConstants.ApiPath.SWAGGER_PATH,
                                ApiPathMatchers.LOAN_TYPE_MATCHER,
                                ApiPathMatchers.STATE_MATCHER,
                                ApiPathMatchers.TEST_MATCHER
                        ).permitAll()
                        .pathMatchers(
                                ApiPathMatchers.HEALTH_CHECK_MATCHER
                        ).permitAll()
                        .pathMatchers(
                                ApiPathMatchers.ACTUATOR_MATCHER
                        ).hasAnyAuthority(
                                ApiConstants.Role.SUPER_USER_ROLE_NAME
                        )
                        .pathMatchers(
                                HttpMethod.POST, ApiPathMatchers.DEBT_CAPACITY_MATCHER
                        ).hasAnyAuthority(
                                ApiConstants.Role.CALLBACK_ROLE
                        ).pathMatchers(
                                HttpMethod.POST, ApiPathMatchers.SOLICITUDE_MATCHER
                        ).hasAnyAuthority(
                                ApiConstants.Role.CLIENT_ROLE_NAME
                        ).pathMatchers(
                                HttpMethod.GET, ApiPathMatchers.SOLICITUDE_MATCHER
                        ).hasAnyAuthority(
                                ApiConstants.Role.ADVISOR_ROLE_NAME
                        ).pathMatchers(
                                HttpMethod.PUT, ApiPathMatchers.SOLICITUDE_MATCHER
                        ).hasAnyAuthority(
                                ApiConstants.Role.ADVISOR_ROLE_NAME
                        ).anyExchange().authenticated()
                )
                .exceptionHandling(spec -> spec
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .build();
    }

    private class JwtSecurityContextRepository implements ServerSecurityContextRepository {
        @Override
        public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
            return Mono.empty();
        }

        @Override
        public Mono<SecurityContext> load(ServerWebExchange exchange) {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token != null && token.startsWith("Bearer ")) {
                String authToken = token.substring(7);
                try {
                    JwtData jwtData = jwtProvider.getClaims(authToken);
                    String email = jwtData.subject();
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(jwtData.role()));
                    Authentication auth = new UsernamePasswordAuthenticationToken(email, authToken, authorities);
                    return Mono.just(new SecurityContextImpl(auth));
                } catch (Exception e) {
                    return Mono.error(new InvalidCredentialsException());
                }
            }
            return Mono.empty();
        }
    }
}
