package co.com.pragma.model.loantype;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {
    private Integer loanTypeId;
    private String name;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private BigDecimal interestRate;
    private Boolean autoValidation;

}
