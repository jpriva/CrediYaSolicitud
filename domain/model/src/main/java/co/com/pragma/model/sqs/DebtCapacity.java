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
    private BigDecimal baseSalary;
    private BigDecimal value;
    private BigDecimal interestRate;
    private BigDecimal currentTotalMonthlyFee;
}