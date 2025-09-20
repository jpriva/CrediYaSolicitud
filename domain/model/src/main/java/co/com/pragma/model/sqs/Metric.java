package co.com.pragma.model.sqs;

import java.math.BigDecimal;

public record Metric(
        String name,
        BigDecimal value
) {
}
