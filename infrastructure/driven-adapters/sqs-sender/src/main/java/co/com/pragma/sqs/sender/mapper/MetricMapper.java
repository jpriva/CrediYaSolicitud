package co.com.pragma.sqs.sender.mapper;

import co.com.pragma.model.exceptions.MetricInvalidValueException;
import co.com.pragma.model.exceptions.MetricNotConfiguredException;
import co.com.pragma.model.exceptions.MetricNotFoundException;
import co.com.pragma.model.exceptions.MetricsNotConfiguredException;
import co.com.pragma.model.sqs.Metric;
import co.com.pragma.model.sqs.Metrics;
import co.com.pragma.sqs.sender.config.QueueAlias;
import co.com.pragma.sqs.sender.dto.MetricDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricMapper {

    public static Mono<MetricDTO> toSqsMessage(Metric metric, Map<String, String> tags) {
        if (metric == null || metric.name() == null || metric.name().isBlank()) {
            return Mono.error(new MetricNotFoundException("null"));
        }
        if (metric.value() == null || metric.value().compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new MetricInvalidValueException(metric.name()));
        }
        if (tags == null || tags.isEmpty()) {
            return Mono.error(new MetricsNotConfiguredException());
        }

        return switch (metric.name()) {
            case Metrics.QUANTITY_METRIC -> buildMetricDto(tags, QueueAlias.MetricNames.QUANTITY, metric.value());
            case Metrics.AMOUNT_METRIC -> buildMetricDto(tags, QueueAlias.MetricNames.AMOUNT, metric.value());
            default -> Mono.error(new MetricNotFoundException(metric.name()));
        };
    }

    private static Mono<MetricDTO> buildMetricDto(Map<String, String> tags, String metricKey, BigDecimal value) {
        return Mono.justOrEmpty(tags.get(metricKey))
                .switchIfEmpty(Mono.error(new MetricNotConfiguredException(metricKey)))
                .map(name -> MetricDTO.builder()
                        .name(name)
                        .value(value)
                        .build());
    }
}
