package co.com.pragma.model.solicitude;
import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.state.State;
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
public class Solicitude {
    private Integer solicitudeId;
    private BigDecimal value;
    private Integer deadline;
    private String email;
    private State state;
    private LoanType loanType;

    public void trim() {
        this.email = this.email.trim();
        if (this.value != null){
            this.value = this.value.setScale(2, DefaultValues.DEFAULT_ROUNDING_MODE);
        }
    }
}
