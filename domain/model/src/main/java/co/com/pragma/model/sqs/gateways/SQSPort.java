package co.com.pragma.model.sqs.gateways;

import co.com.pragma.model.sqs.DebtCapacity;
import co.com.pragma.model.sqs.Metric;
import co.com.pragma.model.template.EmailMessage;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SQSPort {

    Mono<Void> sendEmail(EmailMessage emailMessage);

    Mono<Void> sendDebtCapacity(DebtCapacity debtCapacity);

    Mono<Void> sendMetric(List<Metric> metrics);
}
