package co.com.pragma.sqs.sender.dto;

import co.com.pragma.sqs.sender.util.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MetricDTO {
    private String name;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal value;
}
