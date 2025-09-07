package co.com.pragma.api.state;

import co.com.pragma.api.constants.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class StateRouterRest {

    @Bean
    public RouterFunction<ServerResponse> stateRouterFunction(StateHandler handler) {
        return RouterFunctions.route()
                .GET(ApiConstants.ApiPath.STATE_PATH, handler::listenGETStateUseCase)
                .build();
    }
}