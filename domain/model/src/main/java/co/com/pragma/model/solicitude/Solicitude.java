package co.com.pragma.model.solicitude;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.state.State;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitude {
    private Integer solicitudeId;
    private BigDecimal value;
    private Integer deadline;
    private String email;
    private State state;
    private LoanType loanType;
}
