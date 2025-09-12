package co.com.pragma.model.sqs.gateways;

import co.com.pragma.model.sqs.DebtCapacity;
import reactor.core.publisher.Mono;

public interface SQSPort {
    Mono<Void> sendEmail(String email, String title, String message);
    Mono<Void> sendDebtCapacity(DebtCapacity debtCapacity);
}
