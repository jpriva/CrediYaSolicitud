package co.com.pragma.api.loantype;

import co.com.pragma.api.constants.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LoanTypeRouterRest {

    @Bean
    public RouterFunction<ServerResponse> loanTypeRouterFunction(LoanTypeHandler handler) {
        return RouterFunctions.route()
                .GET(ApiConstants.ApiPath.LOAN_TYPE_PATH, handler::listenGETLoanTypeUseCase)
                .build();
    }
}
