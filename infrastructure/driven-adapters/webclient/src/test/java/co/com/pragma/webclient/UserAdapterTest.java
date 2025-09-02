package co.com.pragma.webclient;

import co.com.pragma.model.logs.gateways.LoggerPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

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
}