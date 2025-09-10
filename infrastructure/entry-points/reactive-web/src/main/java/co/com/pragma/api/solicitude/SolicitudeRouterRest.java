package co.com.pragma.api.solicitude;

import co.com.pragma.api.constants.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SolicitudeRouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(SolicitudeHandler handler) {
        return RouterFunctions.route()
                .POST(ApiConstants.ApiPath.SOLICITUDE_PATH, handler::listenPOSTSaveSolicitudeUseCase)
                .GET(ApiConstants.ApiPath.SOLICITUDE_PATH, handler::listenGETSolicitudeReportUseCase)
                .PUT(ApiConstants.ApiPath.SOLICITUDE_UPDATE_PATH, handler::listenPUTUpdateSolicitudeStatusUseCase)
                .build();
    }
}
