package co.com.pragma.api;

import co.com.pragma.api.constants.Constants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.SolicitudeRequestDTO;
import co.com.pragma.api.dto.SolicitudeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static co.com.pragma.api.constants.Constants.API_SOLICITUDE_PATH;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = API_SOLICITUDE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTSaveSolicitudeUseCase",
                    operation = @Operation(
                            operationId = Constants.OPERATION_SAVE_SOLICITUDE_ID,
                            requestBody = @RequestBody(
                                    content = @Content(
                                            schema = @Schema(implementation = SolicitudeRequestDTO.class)
                                    ),
                                    required = true,
                                    description = Constants.OPERATION_SAVE_SOLICITUDE_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = Constants.RESPONSE_CREATED_CODE,
                                            description = Constants.RESPONSE_SAVE_SOLICITUDE_CREATED_DESC,
                                            content = @Content(schema = @Schema(implementation = SolicitudeResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = Constants.RESPONSE_BAD_REQUEST_CODE,
                                            description = Constants.RESPONSE_SAVE_SOLICITUDE_BAD_REQUEST_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = Constants.RESPONSE_CONFLICT_CODE,
                                            description = Constants.RESPONSE_SAVE_SOLICITUDE_CONFLICT_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return RouterFunctions.route()
                .POST(API_SOLICITUDE_PATH, handler::listenPOSTSaveSolicitudeUseCase)
                .build();
    }
}
