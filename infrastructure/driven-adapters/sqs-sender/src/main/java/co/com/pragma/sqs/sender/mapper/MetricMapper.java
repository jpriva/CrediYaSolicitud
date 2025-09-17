package co.com.pragma.sqs.sender.mapper;

import co.com.pragma.model.exceptions.MetricInvalidValueException;
import co.com.pragma.model.exceptions.MetricNotConfiguredException;
import co.com.pragma.model.exceptions.MetricNotFoundException;
import co.com.pragma.model.exceptions.MetricsNotConfiguredException;
import co.com.pragma.model.sqs.Metric;
import co.com.pragma.model.sqs.Metrics;
import co.com.pragma.sqs.sender.config.QueueAlias;
import co.com.pragma.sqs.sender.dto.MetricDTO;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MetricMapper {
    public static Mono<MetricDTO> toSqsMessage(Metric metric, Map<String, String> tags) {
        if (metric == null || metric.name() == null || metric.name().isBlank()) return Mono.error(new MetricNotFoundException("null"));
        if (metric.value() == null || metric.value().compareTo(BigDecimal.ZERO) < 0) return Mono.error(new MetricInvalidValueException(metric.name()));
        if(tags == null || tags.isEmpty()) return Mono.error(new MetricsNotConfiguredException());
        MetricDTO.MetricDTOBuilder builder = MetricDTO.builder()
                .value(metric.value());
        if (metric.name().equals(Metrics.QUANTITY_METRIC)){
            if(!tags.containsKey(QueueAlias.MetricNames.QUANTITY)) return Mono.error(new MetricNotConfiguredException(QueueAlias.MetricNames.QUANTITY));
            return Mono.just(
                    builder.name(tags.get(QueueAlias.MetricNames.QUANTITY))
                    .build()
            );
        }
        return Mono.error(new MetricNotFoundException(metric.name()));
    }
}