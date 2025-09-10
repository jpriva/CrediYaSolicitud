package co.com.pragma.sqs.sender;

import co.com.pragma.model.sqs.gateways.SQSPort;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.sqs.sender.dto.EmailSqsMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SQSPort {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public void sendEmail(String email, String title, String message) {
        EmailSqsMessage payload = EmailSqsMessage.builder()
                .to(email)
                .subject(title)
                .body(message)
                .build();
        try {
            String jsonMessage = objectMapper.writeValueAsString(payload);
            log.info("Sending email notification to SQS for recipient: [{}] : {}", email, jsonMessage);
            this.send(jsonMessage)
                    .subscribe(
                            messageId -> log.info("Email notification for {} queued successfully with Message ID: {}", email, messageId),
                            error -> log.error("Failed to send email notification to SQS for recipient: {}. Error: {}", email, error.getMessage())
                    );

        } catch (JsonProcessingException e) {
            log.error("Error creating JSON payload for email notification. Recipient: {}. Error: {}", email, e.getMessage());
        }
    }
}
