package co.com.pragma.webclient.config;

import co.com.pragma.model.logs.gateways.LoggerPort;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WebClientConfig.class)
class WebClientConfigTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private WebClient webClient;

    @MockitoBean
    private LoggerPort logger;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry){
        registry.add("adapters.restconsumer.url", () -> mockWebServer.url("/").toString());
        registry.add("adapters.restconsumer.timeout", () -> "5000");
    }

    @Test
    void shouldAddAuthHeaderWhenContextHasToken() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"ok\"}"));
        String dummyToken = "dummy-jwt-token";
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", dummyToken);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        Mono<String> responseMono = webClient.get()
                .uri("/test")
                .retrieve()
                .bodyToMono(String.class)
                .contextWrite(Context.of(SecurityContext.class, Mono.just(securityContext)));

        StepVerifier.create(responseMono)
                .expectNext("{\"status\":\"ok\"}")
                .verifyComplete();

        var recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("Bearer " + dummyToken);
    }

    @Test
    void shouldNotAddAuthHeaderWhenContextIsEmpty() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"ok\"}"));

        StepVerifier.create(webClient.get().uri("/test").retrieve().bodyToMono(String.class))
                .expectNext("{\"status\":\"ok\"}")
                .verifyComplete();

        var recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization")).isNull();
    }

    @Test
    void shouldLogWarningWhenAuthHasNoCredentials() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"ok\"}"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        Mono<String> responseMono = webClient.get()
                .uri("/test")
                .retrieve()
                .bodyToMono(String.class)
                .contextWrite(Context.of(SecurityContext.class, Mono.just(securityContext)));

        StepVerifier.create(responseMono).expectNextCount(1).verifyComplete();

        var recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getHeader("Authorization")).isNull();
        verify(logger).warn("Could not find JWT in credentials to propagate to downstream service. Proceeding without Authorization header.");
    }

    @Test
    void shouldLogRequestAndResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(201));

        StepVerifier.create(webClient.post().uri("/log-test").retrieve().toBodilessEntity())
                .expectNextCount(1)
                .verifyComplete();

        mockWebServer.takeRequest();

        verify(logger).info(eq("Request: {} {}"), eq(HttpMethod.POST), any());
        verify(logger).info(eq("Response Status: {}"), any());
    }
}