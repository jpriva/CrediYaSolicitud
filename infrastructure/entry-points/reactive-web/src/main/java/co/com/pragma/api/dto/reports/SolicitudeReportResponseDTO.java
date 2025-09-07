package co.com.pragma.api.dto.reports;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.util.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = ApiConstants.Report.REPORT_RES_SCHEMA_NAME, description = ApiConstants.Report.REPORT_RES_SCHEMA_DESC)
public class SolicitudeReportResponseDTO {
    @Schema(description = ApiConstants.Report.REPORT_RES_SOLICITUDE_ID_DESC, example = ApiConstants.Report.EXAMPLE_RES_SOLICITUDE_ID)
    private Integer solicitudeId;

    @JsonSerialize(using = MoneySerializer.class)
    @Schema(description = ApiConstants.Report.REPORT_RES_VALUE_DESC, example = ApiConstants.Report.EXAMPLE_RES_VALUE)
    private BigDecimal value;

    @Schema(description = ApiConstants.Report.REPORT_RES_DEADLINE_DESC, example = ApiConstants.Report.EXAMPLE_RES_DEADLINE)
    private Integer deadline;

    @Schema(description = ApiConstants.Report.REPORT_RES_CLIENT_EMAIL_DESC, example = ApiConstants.Report.EXAMPLE_RES_CLIENT_EMAIL)
    private String clientEmail;

    @Schema(description = ApiConstants.Report.REPORT_RES_CLIENT_NAME_DESC, example = ApiConstants.Report.EXAMPLE_RES_CLIENT_NAME)
    private String clientName;

    @Schema(description = ApiConstants.Report.REPORT_RES_CLIENT_ID_DESC, example = ApiConstants.Report.EXAMPLE_RES_CLIENT_ID)
    private String clientIdentification;

    @Schema(description = ApiConstants.Report.REPORT_RES_LOAN_TYPE_DESC, example = ApiConstants.Report.EXAMPLE_RES_LOAN_TYPE)
    private String loanTypeName;

    @JsonSerialize(using = MoneySerializer.class)
    @Schema(description = ApiConstants.Report.REPORT_RES_INTEREST_RATE_DESC, example = ApiConstants.Report.EXAMPLE_RES_INTEREST_RATE)
    private BigDecimal interestRate;

    @Schema(description = ApiConstants.Report.REPORT_RES_STATE_DESC, example = ApiConstants.Report.EXAMPLE_RES_STATE)
    private String stateName;

    @JsonSerialize(using = MoneySerializer.class)
    @Schema(description = ApiConstants.Report.REPORT_RES_BASE_SALARY_DESC, example = ApiConstants.Report.EXAMPLE_RES_BASE_SALARY)
    private BigDecimal baseSalary;

    @JsonSerialize(using = MoneySerializer.class)
    @Schema(description = ApiConstants.Report.REPORT_RES_TOTAL_DEBT_DESC, example = ApiConstants.Report.EXAMPLE_RES_TOTAL_DEBT)
    private BigDecimal totalMonthlyPay;
}
