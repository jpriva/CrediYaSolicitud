package co.com.pragma.sqs.sender;

import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.sqs.sender.dto.EmailSqsMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSSenderTest {

    @Mock
    private SQSSenderProperties properties;
    @Mock
    private SqsAsyncClient client;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    @Spy // Usamos Spy para poder mockear el método 'send' dentro de la misma clase
    private SQSSender sqsSender;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @Test
    void sendEmail_shouldSerializePayloadAndCallSend() throws JsonProcessingException {
        // Arrange
        String email = "test@example.com";
        String title = "Test Title";
        String message = "Test Message";
        String expectedJson = "{\"to\":\"test@example.com\",\"subject\":\"Test Title\",\"body\":\"Test Message\"}";

        when(objectMapper.writeValueAsString(any(EmailSqsMessage.class))).thenReturn(expectedJson);
        // Mockeamos la llamada al método 'send' para aislar la prueba a 'sendEmail'
        doReturn(Mono.just("dummy-message-id")).when(sqsSender).send(any());

        // Act
        sqsSender.sendEmail(email, title, message);

        // Assert
        verify(objectMapper).writeValueAsString(any(EmailSqsMessage.class));
        verify(sqsSender).send(messageCaptor.capture());
        assertEquals(expectedJson, messageCaptor.getValue());
    }

    @Test
    void sendEmail_shouldHandleSerializationError() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(any(EmailSqsMessage.class))).thenThrow(new JsonProcessingException("Serialization failed") {});

        // Act
        sqsSender.sendEmail("test@example.com", "title", "message");

        // Assert
        // Verificamos que el método 'send' nunca fue llamado si la serialización falla
        verify(sqsSender, never()).send(any());
    }

    @Test
    void send_shouldSendMessageAndReturnMessageId() {
        // Arrange
        String testMessage = "{\"test\":\"message\"}";
        String queueUrl = "http://test.queue.url";
        String expectedMessageId = "id-12345";

        when(properties.queueUrl()).thenReturn(queueUrl);

        SendMessageResponse response = SendMessageResponse.builder().messageId(expectedMessageId).build();
        CompletableFuture<SendMessageResponse> futureResponse = CompletableFuture.completedFuture(response);
        when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(futureResponse);

        // Act
        Mono<String> result = sqsSender.send(testMessage);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedMessageId)
                .verifyComplete();

        ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(client).sendMessage(requestCaptor.capture());
        assertEquals(queueUrl, requestCaptor.getValue().queueUrl());
        assertEquals(testMessage, requestCaptor.getValue().messageBody());
    }

    @Test
    void send_shouldReturnErrorOnSqsFailure() {
        // Arrange
        String testMessage = "{\"test\":\"message\"}";
        when(properties.queueUrl()).thenReturn("http://test.queue.url");

        CompletableFuture<SendMessageResponse> futureResponse = CompletableFuture.failedFuture(SdkClientException.create("SQS is down"));
        when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(futureResponse);

        // Act
        Mono<String> result = sqsSender.send(testMessage);

        // Assert
        StepVerifier.create(result)
                .expectError(SdkClientException.class)
                .verify();
    }
}