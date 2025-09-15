package co.com.pragma.model.sqs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
public class DebtCapacity {
    private Integer solicitudeId;
    private String email;
    private BigDecimal baseSalary;
    private BigDecimal value;
    private BigDecimal interestRate;
    private Integer deadline;
    private BigDecimal currentTotalMonthlyFee;
}