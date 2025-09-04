package co.com.pragma.api.solicitude.documentation;

import co.com.pragma.api.solicitude.SolicitudeHandler;
import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.constants.Constants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.reports.SolicitudeReportRequestDTO;
import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class ReportsDocumentation {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = ApiConstants.ApiPath.API_REPORT_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = SolicitudeHandler.class,
                    beanMethod = "listenPOSTSolicitudeReportUseCase",
                    operation = @Operation(
                            operationId = Constants.OPERATION_REPORT_SOLICITUDE_ID,
                            requestBody = @RequestBody(
                                    content = @Content(
                                            schema = @Schema(implementation = SolicitudeReportRequestDTO.class)
                                    ),
                                    required = true,
                                    description = Constants.OPERATION_REPORT_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = Constants.RESPONSE_OK_CODE,
                                            description = Constants.RESPONSE_REPORT_OK_DESC,
                                            content = @Content(schema = @Schema(implementation = SolicitudeReportResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = Constants.RESPONSE_BAD_REQUEST_CODE,
                                            description = Constants.RESPONSE_SAVE_SOLICITUDE_BAD_REQUEST_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> reportDocumentationRoutes() {
        return RouterFunctions.route(GET(ApiConstants.ApiPath.DUMMY_REPORT_DOC_ROUTE), req -> ServerResponse.ok().build());
    }
}
