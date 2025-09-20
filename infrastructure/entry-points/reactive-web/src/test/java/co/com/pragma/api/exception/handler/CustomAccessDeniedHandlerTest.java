package co.com.pragma.api.exception.handler;

import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.model.constants.Errors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private ServerHttpResponse response;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private RequestPath path;
    @Mock
    private HttpHeaders headers;
    @Mock
    private DataBufferFactory bufferFactory;
    @Mock
    private DataBuffer dataBuffer;

    @InjectMocks
    private CustomAccessDeniedHandler accessDeniedHandler;

    @BeforeEach
    void setUp() {
        // Mockear la cadena de objetos de ServerWebExchange
        when(exchange.getResponse()).thenReturn(response);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(path);
        when(path.value()).thenReturn("/test/forbidden");
        when(response.getHeaders()).thenReturn(headers);
        when(response.bufferFactory()).thenReturn(bufferFactory);

    }

    @Test
    @DisplayName("Should set 403 status and write JSON error body on access denied")
    void handle_shouldSetForbiddenStatusAndWriteErrorBody() throws JsonProcessingException {
        // Arrange
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access Denied");
        byte[] errorBytes = "{\"error\":\"test\"}".getBytes();

        when(objectMapper.writeValueAsBytes(any(ErrorDTO.class))).thenReturn(errorBytes);
        when(bufferFactory.wrap(errorBytes)).thenReturn(dataBuffer);
        when(response.writeWith(any(Publisher.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = accessDeniedHandler.handle(exchange, accessDeniedException);

        // Assert
        StepVerifier.create(result).verifyComplete();

        // Verificar los efectos secundarios inmediatos
        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(headers).setContentType(MediaType.APPLICATION_JSON);

        // Capturar el Publisher para desencadenar la lógica interna
        ArgumentCaptor<Publisher<DataBuffer>> publisherCaptor = ArgumentCaptor.forClass(Publisher.class);
        verify(response).writeWith(publisherCaptor.capture());

        StepVerifier.create(publisherCaptor.getValue())
                .expectNext(dataBuffer)
                .verifyComplete();

        // Ahora que el Publisher se ha ejecutado, verificar la serialización
        ArgumentCaptor<ErrorDTO> dtoCaptor = ArgumentCaptor.forClass(ErrorDTO.class);
        verify(objectMapper).writeValueAsBytes(dtoCaptor.capture());
        assertEquals(Errors.ACCESS_DENIED_CODE, dtoCaptor.getValue().getCode());
        assertEquals("/test/forbidden", dtoCaptor.getValue().getPath());
    }
}