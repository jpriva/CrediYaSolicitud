package co.com.pragma.api.solicitude.documentation;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.ErrorDTO;
import co.com.pragma.api.dto.reports.SolicitudeReportPaginatedResponseDTO;
import co.com.pragma.api.solicitude.SolicitudeHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
                    path = ApiConstants.ApiPath.SOLICITUDE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = SolicitudeHandler.class,
                    beanMethod = "listenGETSolicitudeReportUseCase",
                    operation = @Operation(
                            summary = ApiConstants.ReportDocs.REPORT_OP_SUMMARY,
                            description = ApiConstants.ReportDocs.REPORT_OP_DESC,
                            operationId = ApiConstants.ApiOperations.OPERATION_REPORT_SOLICITUDE_ID,
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.CLIENT_EMAIL, description = ApiConstants.ReportDocs.PARAM_CLIENT_EMAIL_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_STRING)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.CLIENT_NAME, description = ApiConstants.ReportDocs.PARAM_CLIENT_NAME_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_STRING)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.CLIENT_IDENTIFICATION, description = ApiConstants.ReportDocs.PARAM_CLIENT_ID_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_STRING)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.LOAN_TYPE, description = ApiConstants.ReportDocs.PARAM_LOAN_TYPE_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_STRING)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.STATE, description = ApiConstants.ReportDocs.PARAM_STATE_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_STRING)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.MIN_VALUE, description = ApiConstants.ReportDocs.PARAM_MIN_VALUE_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_NUMBER)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.MAX_VALUE, description = ApiConstants.ReportDocs.PARAM_MAX_VALUE_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_NUMBER)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.MIN_BASE_SALARY, description = ApiConstants.ReportDocs.PARAM_MIN_SALARY_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_NUMBER)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.MAX_BASE_SALARY, description = ApiConstants.ReportDocs.PARAM_MAX_SALARY_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_NUMBER)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.PAGE, description = ApiConstants.ReportDocs.PARAM_PAGE_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_INTEGER, defaultValue = ApiConstants.Pageable.DEFAULT_PAGE)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.SIZE, description = ApiConstants.ReportDocs.PARAM_SIZE_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_INTEGER, defaultValue = ApiConstants.Pageable.DEFAULT_SIZE)),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.SORT_BY, description = ApiConstants.ReportDocs.PARAM_SORT_BY_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_STRING, allowableValues = {ApiConstants.Pageable.SORT_BY_CLIENT_EMAIL, ApiConstants.Pageable.SORT_BY_VALUE, ApiConstants.Pageable.SORT_BY_LOAN_TYPE, ApiConstants.Pageable.SORT_BY_STATE})),
                                    @Parameter(in = ParameterIn.QUERY, name = ApiConstants.FilterParams.SORT_DIRECTION, description = ApiConstants.ReportDocs.PARAM_SORT_DIR_DESC, schema = @Schema(type = ApiConstants.ReportDocs.RES_TYPE_STRING, allowableValues = {ApiConstants.Pageable.SORT_DIR_ASC, ApiConstants.Pageable.SORT_DIR_DESC}))
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_OK_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_REPORT_OK_DESC,
                                            content = @Content(schema = @Schema(implementation = SolicitudeReportPaginatedResponseDTO.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = ApiConstants.ApiResponses.RESPONSE_BAD_REQUEST_CODE,
                                            description = ApiConstants.ApiResponses.RESPONSE_SAVE_SOLICITUDE_BAD_REQUEST_DESC,
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
