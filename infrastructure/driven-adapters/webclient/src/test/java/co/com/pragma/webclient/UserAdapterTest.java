package co.com.pragma.webclient;

import co.com.pragma.model.exceptions.UserGatewayException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.webclient.config.WebClientProperties;
import co.com.pragma.webclient.dto.UserDTO;
import co.com.pragma.webclient.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdapterTest {

    private UserAdapter userAdapter;
    private MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LoggerPort logger;

    @Mock
    private UserMapper userMapper;

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClientProperties properties = new WebClientProperties();
        properties.setUrl(mockWebServer.url("/").toString());
        properties.setPathUsersByEmails("/api/v1/busquedas/emails");
        properties.setPathUserByEmail("/api/v1/busquedas/email/");
        properties.setPathUsersByFilter("/api/v1/busquedas/filter");

        WebClient webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();

        userAdapter = new UserAdapter(properties, webClient, logger, userMapper);
    }

    @Nested
    class GetUsersByEmailsTests {

        @Test
        void shouldReturnUsersWhenServiceSucceeds() throws JsonProcessingException {
            UserDTO userDTO1 = new UserDTO();
            userDTO1.setEmail("test1@test.com");
            UserDTO userDTO2 = new UserDTO();
            userDTO2.setEmail("test2@test.com");
            List<UserDTO> dtoList = List.of(userDTO1, userDTO2);

            UserProjection projection1 = new UserProjection("test1@test.com", "Test One", "1", BigDecimal.ONE);
            UserProjection projection2 = new UserProjection("test2@test.com", "Test Two", "2", BigDecimal.ONE);

            mockWebServer.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(dtoList))
                    .addHeader("Content-Type", "application/json"));

            when(userMapper.toProjection(userDTO1)).thenReturn(projection1);
            when(userMapper.toProjection(userDTO2)).thenReturn(projection2);

            StepVerifier.create(userAdapter.getUsersByEmails(List.of("test1@test.com", "test2@test.com")))
                    .expectNext(projection1, projection2)
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenEmailListIsEmpty() {
            StepVerifier.create(userAdapter.getUsersByEmails(List.of()))
                    .verifyComplete();
        }

        @Test
        void shouldThrowGatewayExceptionOn4xxError() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));

            StepVerifier.create(userAdapter.getUsersByEmails(List.of("any@email.com")))
                    .expectError(UserGatewayException.class)
                    .verify();
        }

        @Test
        void shouldThrowGatewayExceptionOnCommunicationError() throws IOException {
            mockWebServer.shutdown();

            StepVerifier.create(userAdapter.getUsersByEmails(List.of("any@email.com")))
                    .expectError(WebClientRequestException.class)
                    .verify();
        }
    }

    @Nested
    class GetUserByEmailTests {

        @Test
        void shouldReturnUserWhenServiceSucceeds() throws JsonProcessingException {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail("single@test.com");
            UserProjection projection = new UserProjection("single@test.com", "Single User", "3", BigDecimal.ONE);

            mockWebServer.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(userDTO))
                    .addHeader("Content-Type", "application/json"));

            when(userMapper.toProjection(any(UserDTO.class))).thenReturn(projection);

            StepVerifier.create(userAdapter.getUserByEmail("single@test.com"))
                    .expectNext(projection)
                    .verifyComplete();
        }

        @Test
        void shouldReturnEmptyWhenServiceReturns404() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(404));

            StepVerifier.create(userAdapter.getUserByEmail("notfound@test.com"))
                    .verifyComplete(); // A 404 should result in an empty Mono after onStatus handling
        }

        @Test
        void shouldThrowGatewayExceptionOn5xxError() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(503));

            StepVerifier.create(userAdapter.getUserByEmail("any@email.com"))
                    .verifyComplete();
        }
    }

    @Nested
    class GetUserByFilterTests {

        @Test
        void shouldReturnUsersWhenServiceSucceeds() throws JsonProcessingException, InterruptedException {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().clientName("John").build();
            UserDTO userDTO = new UserDTO();
            userDTO.setName("John Doe");
            List<UserDTO> dtoList = List.of(userDTO);
            UserProjection projection = new UserProjection("john.doe@test.com", "John Doe", "4", BigDecimal.ONE);

            mockWebServer.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(dtoList))
                    .addHeader("Content-Type", "application/json"));

            when(userMapper.toProjection(any(UserDTO.class))).thenReturn(projection);

            StepVerifier.create(userAdapter.getUserByFilter(filter))
                    .expectNext(projection)
                    .verifyComplete();

            var request = mockWebServer.takeRequest();
            Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/busquedas/filter?name=John");
        }

        @Test
        void shouldReturnEmptyWhenFilterIsNull() {
            StepVerifier.create(userAdapter.getUserByFilter(null))
                    .verifyComplete();
        }

        @Test
        void shouldThrowGatewayExceptionOn5xxError() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(503).setBody("Service Unavailable"));

            StepVerifier.create(userAdapter.getUserByFilter(new SolicitudeReportFilter()))
                    .expectError(UserGatewayException.class)
                    .verify();
        }

        @Test
        void shouldBuildUriWithNoFilters() throws InterruptedException {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().build();
            mockWebServer.enqueue(new MockResponse().setBody("[]").addHeader("Content-Type", "application/json"));

            StepVerifier.create(userAdapter.getUserByFilter(filter))
                    .verifyComplete();

            var request = mockWebServer.takeRequest();
            Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/busquedas/filter");
        }

        @Test
        void shouldBuildUriWithMultipleFilters() throws InterruptedException {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .clientEmail("test@test.com")
                    .minBaseSalary(new BigDecimal("50000"))
                    .build();
            mockWebServer.enqueue(new MockResponse().setBody("[]").addHeader("Content-Type", "application/json"));

            StepVerifier.create(userAdapter.getUserByFilter(filter))
                    .verifyComplete();

            var request = mockWebServer.takeRequest();
            Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/busquedas/filter?email=test@test.com&minBaseSalary=50000");
        }

        @Test
        void shouldThrowGatewayExceptionOnCommunicationError() throws IOException {
            mockWebServer.shutdown();

            StepVerifier.create(userAdapter.getUserByFilter(new SolicitudeReportFilter()))
                    .expectError(WebClientRequestException.class)
                    .verify();
        }
    }
}