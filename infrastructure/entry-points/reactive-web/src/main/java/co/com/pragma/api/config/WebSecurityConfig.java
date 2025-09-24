package co.com.pragma.api.config;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.constants.ApiConstants.ApiPathMatchers;
import co.com.pragma.api.exception.handler.CustomAccessDeniedHandler;
import co.com.pragma.api.exception.handler.CustomAuthenticationEntryPoint;
import co.com.pragma.model.logs.gateways.LoggerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CallbackTokenAuthenticationManager callbackTokenAuthenticationManager;
    private final CallbackTokenAuthenticationConverter callbackTokenAuthenticationConverter;
    private final LoggerPort logger;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter callbackFilter = new AuthenticationWebFilter(callbackTokenAuthenticationManager);
        callbackFilter.setServerAuthenticationConverter(callbackTokenAuthenticationConverter);
        callbackFilter.setRequiresAuthenticationMatcher(
                new PathPatternParserServerWebExchangeMatcher(ApiPathMatchers.DEBT_CAPACITY_MATCHER, HttpMethod.POST)
        );
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .oauth2ResourceServer(spec -> {
                    spec.jwt(jwt ->
                                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                            )
                            .authenticationEntryPoint(authenticationEntryPoint);
                })
                .addFilterAt(callbackFilter, SecurityWebFiltersOrder.AUTHENTICATION)
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

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> {
            Collection<String> roles = extractRoles(jwt);
            var authorities = roles.stream()
                    .filter(r -> r != null && !r.isBlank())
                    .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            return Mono.just(new JwtAuthenticationToken(jwt, authorities, jwt.getSubject()));
        };
    }

    private Collection<String> extractRoles(Jwt jwt) {
        Object single = jwt.getClaims().get("role");
        Object multiple = jwt.getClaims().get("roles");

        if (multiple instanceof Collection<?> col) {
            return col.stream().map(Object::toString).collect(Collectors.toSet());
        }
        if (single instanceof String s) {
            return Set.of(s);
        }
        return List.of();
    }
}
