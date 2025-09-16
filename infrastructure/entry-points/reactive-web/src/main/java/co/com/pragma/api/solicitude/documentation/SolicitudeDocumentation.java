package co.com.pragma.api.solicitude.documentation;

import co.com.pragma.api.solicitude.SolicitudeHandler;
import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.solicitude.UpdateStatusRequestDTO;
import co.com.pragma.api.dto.solicitude.SolicitudeRequestDTO;
import co.com.pragma.api.dto.solicitude.SolicitudeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
public class SolicitudeDocumentation {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = ApiConstants.ApiPath.SOLICITUDE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = SolicitudeHandler.class,
                    beanMethod = "listenPOSTSaveSolicitudeUseCase",
                    operation = @Operation(
                            summary = ApiConstants.SolicitudeDocs.SOLICITUDE_OP_SUMMARY,
                            description = ApiConstants.SolicitudeDocs.SOLICITUDE_OP_DESC,
                            operationId = ApiConstants.ApiOperations.OPERATION_SAVE_SOLICITUDE_ID,
                            requestBody = @RequestBody(
                                    content = @Content(
                                            schema = @Schema(implementation = SolicitudeRequestDTO.class)
                                    ),
                                    required = true,
                                    description = ApiConstants.ApiOperations.OPERATION_SAVE_SOLICITUDE_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_CREATED_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_SAVE_SOLICITUDE_CREATED_DESC,
                                            content = @Content(schema = @Schema(implementation = SolicitudeResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_BAD_REQUEST_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_SAVE_SOLICITUDE_BAD_REQUEST_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_CONFLICT_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_SAVE_SOLICITUDE_CONFLICT_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = ApiConstants.ApiPath.SOLICITUDE_UPDATE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PUT,
                    beanClass = SolicitudeHandler.class,
                    beanMethod = "listenPUTUpdateSolicitudeStatusUseCase",
                    operation = @Operation(
                            operationId = ApiConstants.ApiOperations.OPERATION_UPDATE_SOLICITUDE_STATUS_ID,
                            summary = ApiConstants.StateChangeDocs.APPROVE_REJECT_OP_SUMMARY,
                            description = ApiConstants.StateChangeDocs.APPROVE_REJECT_OP_DESC,
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "id", required = true, description = ApiConstants.SolicitudeDocs.SOLICITUDE_ID_PARAM_DESCRIPTION, schema = @Schema(type = "integer", format = "int32"))
                            },
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = UpdateStatusRequestDTO.class)),
                                    required = true,
                                    description = ApiConstants.SolicitudeDocs.UPDATE_SOLICITUDE_STATUS_BODY_DESC
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_OK_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_UPDATE_SOLICITUDE_OK_DESC,
                                            content = @Content(schema = @Schema(implementation = SolicitudeResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_BAD_REQUEST_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_SAVE_SOLICITUDE_BAD_REQUEST_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_NOT_FOUND_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_UPDATE_SOLICITUDE_NOT_FOUND_DESC,
                                            content = @Content(schema = @Schema(implementation = ErrorDTO.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> solicitudeDocumentationRoutes() {
        return RouterFunctions.route(GET(ApiConstants.ApiPath.DUMMY_SOLICITUDE_DOC_ROUTE), req -> ServerResponse.ok().build());
    }
}
