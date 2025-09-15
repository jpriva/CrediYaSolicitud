package co.com.pragma.sqs.sender;

import co.com.pragma.model.sqs.DebtCapacity;
import co.com.pragma.model.sqs.exceptions.QueueAliasEmptyException;
import co.com.pragma.model.sqs.exceptions.QueueNotFoundException;
import co.com.pragma.model.sqs.gateways.SQSPort;
import co.com.pragma.model.template.EmailMessage;
import co.com.pragma.sqs.sender.config.QueueAlias;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.sqs.sender.mapper.DebtCapacityMapper;
import co.com.pragma.sqs.sender.mapper.EmailMapper;
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
    private final DebtCapacityMapper debtCapacityMapper;
    private final EmailMapper emailMapper;

    public Mono<String> send(String queueAlias, String message) {
        return Mono.justOrEmpty(queueAlias)
                .switchIfEmpty(Mono.error(new QueueAliasEmptyException()))
                .flatMap(alias -> Mono.justOrEmpty(properties.queues().get(alias)))
                .switchIfEmpty(Mono.error(new QueueNotFoundException()))
                .map(queueUrl -> buildRequest(queueUrl, message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String url, String message) {
        return SendMessageRequest.builder()
                .queueUrl(url)
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> sendEmail(EmailMessage emailMessage) {
        var payload = emailMapper.toSqsMessage(emailMessage);
        return sendGenericMessage(QueueAlias.NOTIFY_STATE_CHANGE, payload, "email notification for " + emailMessage.getTo());
    }

    @Override
    public Mono<Void> sendDebtCapacity(DebtCapacity debtCapacity) {
        var payload = debtCapacityMapper.toSqsMessage(debtCapacity);
        return sendGenericMessage(QueueAlias.CALCULATE_DEBT_CAPACITY, payload, "debt capacity calculation");
    }

    private <T> Mono<Void> sendGenericMessage(String queueAlias, T payload, String logContext) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(payload);
            log.info("Sending {} to SQS: {}", logContext, jsonMessage);
            return this.send(queueAlias, jsonMessage)
                    .doOnSuccess(
                            messageId -> log.info("Payload for {} queued successfully with Message ID: {}", logContext, messageId)
                    )
                    .doOnError(
                            error -> log.error("Failed to send payload for {}. Error: {}", logContext, error.getMessage())
                    ).then();
        } catch (JsonProcessingException e) {
            log.error("Error creating JSON payload for {}. Error: {}", logContext, e.getMessage());
            return Mono.error(e);
        }
    }
}
