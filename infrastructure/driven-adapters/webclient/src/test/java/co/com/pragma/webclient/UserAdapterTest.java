package co.com.pragma.webclient;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.UserGatewayException;
import co.com.pragma.model.user.exceptions.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserAdapterTest {

    public static MockWebServer mockWebServer;
    private UserAdapter userAdapter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
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

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        userAdapter = new UserAdapter(webClient, logger);
    }

    @Test
    void shouldReturnUserWhenServiceReturnsSuccess() throws JsonProcessingException {
        User expectedUser = User.builder().idNumber("123").email("test@test.com").build();
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expectedUser))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(userAdapter.getUserByIdNumber("123"))
                .assertNext(user -> {
                    assertThat(user.getIdNumber()).isEqualTo("123");
                    assertThat(user.getEmail()).isEqualTo("test@test.com");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnUserNotFoundExceptionWhenServiceReturns404() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(userAdapter.getUserByIdNumber("404"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnUserGatewayExceptionWhenServiceReturns4xxError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad Request\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(userAdapter.getUserByIdNumber("400"))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserGatewayException &&
                                throwable.getMessage().contains("Bad Request")
                )
                .verify();
    }

    @Test
    void shouldReturnUserGatewayExceptionWhenServiceReturns5xxError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(503)
                .setBody("{\"error\":\"Service Unavailable\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(userAdapter.getUserByIdNumber("503"))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserGatewayException &&
                                throwable.getMessage().contains("Service Unavailable")
                )
                .verify();
    }

    @Test
    void shouldReturnUserGatewayExceptionOnCommunicationError() {
        try {
            mockWebServer.shutdown();
        } catch (IOException e) {
            //***
        }

        StepVerifier.create(userAdapter.getUserByIdNumber("999"))
                .expectError(UserGatewayException.class)
                .verify();

        try {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
        } catch (IOException e) {
            //***
        }
    }
}