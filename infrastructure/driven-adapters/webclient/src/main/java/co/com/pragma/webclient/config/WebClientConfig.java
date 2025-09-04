package co.com.pragma.webclient.config;

import co.com.pragma.model.logs.gateways.LoggerPort;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.concurrent.TimeUnit;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WebClientProperties.class)
public class WebClientConfig {

    private final WebClientProperties properties;
    private final LoggerPort logger;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .wiretap(this.getClass().getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(properties.getTimeout(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(properties.getTimeout(), TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(addAuthHeaderFromContext())
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction addAuthHeaderFromContext() {
        return (clientRequest, next) -> ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    Authentication authentication = context.getAuthentication();

                    if (authentication != null && authentication.getCredentials() instanceof String tokenValue && !tokenValue.isEmpty()) {

                        ClientRequest filteredRequest = ClientRequest.from(clientRequest)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                                .build();

                        return next.exchange(filteredRequest);
                    }
                    if (authentication != null) {
                        logger.warn("Could not find JWT in credentials to propagate to downstream service. Proceeding without Authorization header.");
                    }
                    return next.exchange(clientRequest);
                })
                .switchIfEmpty(next.exchange(clientRequest));
    }
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.info("Response Status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
