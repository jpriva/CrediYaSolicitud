package co.com.pragma.model.sqs.gateways;

import co.com.pragma.model.sqs.DebtCapacity;
import co.com.pragma.model.template.EmailMessage;
import reactor.core.publisher.Mono;

public interface SQSPort {

    Mono<Void> sendEmail(EmailMessage emailMessage);

    Mono<Void> sendDebtCapacity(DebtCapacity debtCapacity);
}
