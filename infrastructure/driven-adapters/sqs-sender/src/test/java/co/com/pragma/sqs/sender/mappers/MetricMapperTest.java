package co.com.pragma.sqs.sender.mappers;

import co.com.pragma.model.exceptions.MetricInvalidValueException;
import co.com.pragma.model.exceptions.MetricNotConfiguredException;
import co.com.pragma.model.exceptions.MetricNotFoundException;
import co.com.pragma.model.exceptions.MetricsNotConfiguredException;
import co.com.pragma.model.sqs.Metric;
import co.com.pragma.model.sqs.Metrics;
import co.com.pragma.sqs.sender.config.QueueAlias;
import co.com.pragma.sqs.sender.dto.MetricDTO;
import co.com.pragma.sqs.sender.mapper.MetricMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetricMapperTest {

    private Map<String, String> validTags;

    @BeforeEach
    void setUp() {
        validTags = Map.of(
                QueueAlias.MetricNames.AMOUNT, "solicitude.amount",
                QueueAlias.MetricNames.QUANTITY, "solicitude.quantity"
        );
    }

    @Test
    void shouldMapAmountMetricToDto() {
        // Arrange
        Metric domain = new Metric(Metrics.AMOUNT_METRIC, new BigDecimal("15000.75"));

        // Act
        Mono<MetricDTO> result = MetricMapper.toSqsMessage(domain, validTags);

        // Assert
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertNotNull(dto);
                    assertEquals("solicitude.amount", dto.getName());
                    assertEquals(0, domain.value().compareTo(dto.getValue()));
                })
                .verifyComplete();
    }

    @Test
    void shouldMapQuantityMetricToDto() {
        // Arrange
        Metric domain = new Metric(Metrics.QUANTITY_METRIC, BigDecimal.ONE);

        // Act
        Mono<MetricDTO> result = MetricMapper.toSqsMessage(domain, validTags);

        // Assert
        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertNotNull(dto);
                    assertEquals("solicitude.quantity", dto.getName());
                    assertEquals(0, domain.value().compareTo(dto.getValue()));
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenMetricIsNull() {
        StepVerifier.create(MetricMapper.toSqsMessage(null, validTags))
                .expectError(MetricNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenValueIsInvalid() {
        Metric domain = new Metric(Metrics.AMOUNT_METRIC, new BigDecimal("-100"));
        StepVerifier.create(MetricMapper.toSqsMessage(domain, validTags))
                .expectError(MetricInvalidValueException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenTagsAreMissing() {
        Metric domain = new Metric(Metrics.AMOUNT_METRIC, BigDecimal.TEN);
        StepVerifier.create(MetricMapper.toSqsMessage(domain, Collections.emptyMap()))
                .expectError(MetricsNotConfiguredException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenMetricIsNotInTags() {
        Metric domain = new Metric(Metrics.AMOUNT_METRIC, BigDecimal.TEN);
        Map<String, String> incompleteTags = Map.of(QueueAlias.MetricNames.QUANTITY, "solicitude.quantity");
        StepVerifier.create(MetricMapper.toSqsMessage(domain, incompleteTags))
                .expectError(MetricNotConfiguredException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenMetricNameIsUnknown() {
        Metric domain = new Metric("unknown.metric", BigDecimal.TEN);
        StepVerifier.create(MetricMapper.toSqsMessage(domain, validTags))
                .expectError(MetricNotFoundException.class)
                .verify();
    }
}