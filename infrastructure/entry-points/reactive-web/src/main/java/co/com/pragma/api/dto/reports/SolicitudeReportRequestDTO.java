package co.com.pragma.api.dto.reports;

import co.com.pragma.api.constants.ApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = ApiConstants.Report.REPORT_REQ_SCHEMA_NAME, description = ApiConstants.Report.REPORT_REQ_SCHEMA_DESC)
public class SolicitudeReportRequestDTO {

    @Schema(description = ApiConstants.Report.REPORT_REQ_CLIENT_EMAIL_DESC,
            example = ApiConstants.Report.EXAMPLE_CLIENT_EMAIL)
    private String clientEmail;

    @Schema(description = ApiConstants.Report.REPORT_REQ_CLIENT_NAME_DESC,
            example = ApiConstants.Report.EXAMPLE_CLIENT_NAME)
    private String clientName;

    @Schema(description = ApiConstants.Report.REPORT_REQ_CLIENT_ID_DESC,
            example = ApiConstants.Report.EXAMPLE_CLIENT_ID)
    private String clientIdNumber;

    @Schema(description = ApiConstants.Report.REPORT_REQ_LOAN_TYPE_DESC,
            example = ApiConstants.Report.EXAMPLE_LOAN_TYPE)
    private String loanTypeName;

    @Schema(description = ApiConstants.Report.REPORT_REQ_STATE_DESC,
            example = ApiConstants.Report.EXAMPLE_STATE)
    private String state;

    @Schema(description = ApiConstants.Report.REPORT_REQ_MIN_VALUE_DESC,
            example = ApiConstants.Report.EXAMPLE_MIN_VALUE)
    private BigDecimal minValue;

    @Schema(description = ApiConstants.Report.REPORT_REQ_MAX_VALUE_DESC,
            example = ApiConstants.Report.EXAMPLE_MAX_VALUE)
    private BigDecimal maxValue;

    @Schema(description = ApiConstants.Report.REPORT_REQ_MIN_SALARY_DESC,
            example = ApiConstants.Report.EXAMPLE_MIN_SALARY)
    private BigDecimal minBaseSalary;

    @Schema(description = ApiConstants.Report.REPORT_REQ_MAX_SALARY_DESC,
            example = ApiConstants.Report.EXAMPLE_MAX_SALARY)
    private BigDecimal maxBaseSalary;

    //***********************************************************************************

    @Min(0)
    @Schema(description = ApiConstants.Report.REPORT_REQ_PAGE_DESC,
            defaultValue = ApiConstants.Report.DEFAULT_PAGE)
    private Integer page = 0;

    @Min(1)
    @Schema(description = ApiConstants.Report.REPORT_REQ_SIZE_DESC,
            defaultValue = ApiConstants.Report.DEFAULT_SIZE)
    private Integer size = 10;

    @Schema(description = ApiConstants.Report.REPORT_REQ_SORT_BY_DESC,
            example = ApiConstants.Report.EXAMPLE_SORT_BY_VALUE,
            allowableValues = {
                    ApiConstants.Report.SORT_BY_VALUE,
                    ApiConstants.Report.SORT_BY_CLIENT_NAME,
                    ApiConstants.Report.SORT_BY_STATE
            })
    private String sortBy;

    @Schema(description = ApiConstants.Report.REPORT_REQ_SORT_DIR_DESC,
            example = ApiConstants.Report.EXAMPLE_SORT_DIR_DESC,
            allowableValues = {
                    ApiConstants.Report.SORT_DIR_ASC,
                    ApiConstants.Report.SORT_DIR_DESC
            })
    private String sortDirection;
}
