package co.com.pragma.webclient.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Value("${adapters.restconsumer.url:https://api.example.com}")
    private String url;

    @Value("${adapters.restconsumer.timeout:5000}")
    private int timeout;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(timeout))
                                .addHandlerLast(new WriteTimeoutHandler(timeout))
                );

        return WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE,"application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
