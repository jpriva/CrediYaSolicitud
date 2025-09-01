package co.com.pragma.api.exception.handler;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.exceptions.CustomException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoggerPort logger;

    @RestController
    static class TestController {
        @GetMapping("/custom-409")
        public Mono<String> throwCustom409() {
            return Mono.error(new CustomException("Conflict error", "DOM-002", 409));
        }

        @GetMapping("/custom-invalid-status")
        public Mono<String> throwCustomInvalidStatus() {
            return Mono.error(new CustomException("Invalid status error", "DOM-003", 999));
        }

        @GetMapping("/generic-error")
        public Mono<String> throwGenericError() {
            return Mono.error(new RuntimeException("A generic error occurred"));
        }

        @PostMapping("/input-error")
        public Mono<String> inputError(@RequestBody DummyBody body) {
            return Mono.just("OK");
        }
    }

    @Setter
    @Getter
    static class DummyBody {
        private String name;

    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void shouldHandleServerWebInputException() {
        String malformedJson = "{\"name\": \"test\"";

        webTestClient.mutateWith(csrf())
                .post().uri("/input-error")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(malformedJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDTO.class)
                .value(error -> {
                    assertThat(error.getCode()).isEqualTo(Errors.FAIL_READ_REQUEST_CODE);
                    assertThat(error.getMessage()).isEqualTo(Errors.FAIL_READ_REQUEST);
                    assertThat(error.getPath()).isEqualTo("/input-error");
                });
    }

    @Test
    @WithMockUser
    void shouldHandleCustomExceptionWithClientErrorStatus() {
        webTestClient.get().uri("/custom-409")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorDTO.class)
                .value(error -> {
                    assertThat(error.getCode()).isEqualTo("DOM-002");
                    assertThat(error.getMessage()).isEqualTo("Conflict error");
                    assertThat(error.getPath()).isEqualTo("/custom-409");
                });
    }

    @Test
    @WithMockUser
    void shouldHandleCustomExceptionWithInvalidStatusAsInternalServerError() {
        webTestClient.get().uri("/custom-invalid-status")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(ErrorDTO.class)
                .value(error -> assertThat(error.getCode()).isEqualTo("DOM-003"));
    }

    @Test
    @WithMockUser
    void shouldHandleGenericExceptionAsInternalServerError() {
        webTestClient.get().uri("/generic-error")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(ErrorDTO.class)
                .value(error -> {
                    assertThat(error.getCode()).isEqualTo(Errors.UNKNOWN_CODE);
                    assertThat(error.getMessage()).isEqualTo(Errors.UNKNOWN_ERROR);
                    assertThat(error.getPath()).isEqualTo("/generic-error");
                });
    }
}