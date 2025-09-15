package co.com.pragma.sqs.sender.dto;

import co.com.pragma.sqs.sender.util.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DebtCapacitySqsMessage {
    private Integer solicitudeId;
    private String email;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal baseSalary;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal value;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal interestRate;
    private Integer deadline;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal currentTotalMonthlyFee;
    private Integer deadline;
    private String token;
}