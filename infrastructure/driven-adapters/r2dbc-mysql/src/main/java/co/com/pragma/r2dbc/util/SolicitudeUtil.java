package co.com.pragma.r2dbc.util;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.state.State;

public class SolicitudeUtil {
    private SolicitudeUtil(){}
    public static Solicitude setSolicitudeLoanTypeAndState(Solicitude solicitude, LoanType loanType, State state){
        if (solicitude == null) {
            return null;
        }
        return solicitude.toBuilder()
                .loanType(loanType)
                .state(state)
                .build();
    }
}
