package co.com.pragma.sqs.sender;

import co.com.pragma.model.sqs.DebtCapacity;
import co.com.pragma.model.sqs.exceptions.QueueAliasEmptyException;
import co.com.pragma.model.sqs.exceptions.QueueNotFoundException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.template.EmailMessage;
import co.com.pragma.sqs.sender.config.QueueAlias;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.sqs.sender.dto.DebtCapacitySqsMessage;
import co.com.pragma.sqs.sender.dto.EmailSqsMessage;
import co.com.pragma.sqs.sender.mapper.DebtCapacityMapper;
import co.com.pragma.sqs.sender.mapper.EmailMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    @Mock
    private DebtCapacityMapper debtCapacityMapper;
    @Mock
    private EmailMapper emailMapper;
    @Mock
    private LoggerPort logger;

    @InjectMocks
    private SQSSender sender;

    @Nested
    @DisplayName("Generic Send Method Tests")
    class SendMethodTests {
        @Test
        @DisplayName("Should send message and return messageId on success")
        void send_shouldSendMessageAndReturnMessageId() {
            // Arrange
            String queueAlias = "test-queue";
            String queueUrl = "http://test.queue.url";
            String testMessage = "{\"test\":\"message\"}";
            String expectedMessageId = "id-12345";


            when(properties.queues()).thenReturn(Map.of(queueAlias, queueUrl));

            SendMessageResponse response = SendMessageResponse.builder().messageId(expectedMessageId).build();
            CompletableFuture<SendMessageResponse> futureResponse = CompletableFuture.completedFuture(response);
            when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(futureResponse);

            // Act
            Mono<String> result = sender.send(queueAlias, testMessage);

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
        @DisplayName("Should return error on SQS client failure")
        void send_shouldReturnErrorOnSqsFailure() {
            // Arrange
            String queueAlias = "test-queue";
            String queueUrl = "http://test.queue.url";
            when(properties.queues()).thenReturn(Map.of(queueAlias, queueUrl));

            CompletableFuture<SendMessageResponse> futureResponse = CompletableFuture.failedFuture(SdkClientException.create("SQS is down"));
            when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(futureResponse);

            // Act
            Mono<String> result = sender.send(queueAlias, "message");

            // Assert
            StepVerifier.create(result)
                    .expectError(SdkClientException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should return error when queue alias is null")
        void send_shouldReturnErrorWhenAliasIsNull() {
            // Act
            Mono<String> result = sender.send(null, "message");

            // Assert
            StepVerifier.create(result)
                    .expectError(QueueAliasEmptyException.class)
                    //.expectErrorMessage("Queue alias cannot be null or empty")
                    .verify();
        }

        @Test
        @DisplayName("Should return error when queue alias is not found")
        void send_shouldReturnErrorWhenAliasIsNotFound() {
            // Arrange
            when(properties.queues()).thenReturn(Map.of("known-alias", "some-url"));

            // Act
            Mono<String> result = sender.send("unknown-alias", "message");

            // Assert
            StepVerifier.create(result)
                    .expectError(QueueNotFoundException.class)
                    //.expectErrorMessage("Queue alias not found in configuration: unknown-alias")
                    .verify();
        }
    }

    @Nested
    @DisplayName("Wrapper Method Tests")
    class WrapperMethodTests {

        @Test
        @DisplayName("sendEmail should serialize and send message successfully")
        void sendEmail_shouldSerializeAndSendMessage() throws JsonProcessingException {
            // Arrange
            EmailMessage message = EmailMessage.builder().to("test@example.com").subject("Test Title").body("Test Message").build();

            String expectedJson = "{\"to\":\"test@example.com\",\"subject\":\"Test Title\",\"body\":\"Test Message\"}";
            EmailSqsMessage sqsMessage = EmailSqsMessage.builder().to("test@example.com").subject("Test Title").body("Test Message").build();
            when(emailMapper.toSqsMessage(message)).thenReturn(sqsMessage);

            when(objectMapper.writeValueAsString(any(EmailSqsMessage.class))).thenReturn(expectedJson);
            when(properties.queues()).thenReturn(Map.of(QueueAlias.NOTIFY_STATE_CHANGE, "some-url"));
            SendMessageResponse mockResponse = SendMessageResponse.builder().messageId("dummy-id").build();
            when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(CompletableFuture.completedFuture(mockResponse));

            // Act
            Mono<Void> result = sender.sendEmail(message);

            // Assert
            StepVerifier.create(result).verifyComplete();

            ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
            verify(client).sendMessage(requestCaptor.capture());
            assertEquals(expectedJson, requestCaptor.getValue().messageBody());
        }

        @Test
        @DisplayName("sendEmail should return error on serialization failure")
        void sendEmail_shouldReturnErrorOnSerializationFailure() throws JsonProcessingException {
            // Arrange
            EmailMessage message = EmailMessage.builder().to("test@example.com").subject("title").body("message").build();
            when(emailMapper.toSqsMessage(message)).thenReturn(EmailSqsMessage.builder().build());

            when(objectMapper.writeValueAsString(any(EmailSqsMessage.class))).thenThrow(new JsonProcessingException("Serialization failed") {
            });

            // Act
            Mono<Void> result = sender.sendEmail(message);

            // Assert
            StepVerifier.create(result)
                    .expectError(JsonProcessingException.class)
                    .verify();
        }

        @Test
        @DisplayName("sendDebtCapacity should call mapper and send message")
        void sendDebtCapacity_shouldCallMapperAndSendMessage() throws JsonProcessingException {
            // Arrange
            DebtCapacity debtCapacity = DebtCapacity.builder().solicitudeId(1).build();
            String expectedJson = "{\"solicitudeId\":1}";

            when(debtCapacityMapper.toSqsMessage(debtCapacity)).thenReturn(DebtCapacitySqsMessage.builder().solicitudeId(1).build());
            when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);
            when(properties.queues()).thenReturn(Map.of(QueueAlias.CALCULATE_DEBT_CAPACITY, "some-url"));
            SendMessageResponse mockResponse = SendMessageResponse.builder().messageId("dummy-id").build();
            when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(CompletableFuture.completedFuture(mockResponse));

            // Act
            Mono<Void> result = sender.sendDebtCapacity(debtCapacity);

            // Assert
            StepVerifier.create(result).verifyComplete();
            verify(debtCapacityMapper).toSqsMessage(debtCapacity);
            verify(client).sendMessage(any(SendMessageRequest.class));
        }
    }
}