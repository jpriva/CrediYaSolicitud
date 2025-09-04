package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = Constants.LOAN_TYPE_SCHEMA_NAME, description = Constants.LOAN_TYPE_SCHEMA_DESCRIPTION)
@Builder(toBuilder = true)
public class LoanTypeResponseDTO {
    @Schema(description = Constants.LOAN_TYPE_ID_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_ID)
    private Integer loanTypeId;
    @Schema(description = Constants.LOAN_TYPE_NAME_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_NAME)
    private String name;
    @Schema(description = Constants.LOAN_TYPE_MIN_VALUE_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_MIN_VALUE)
    private BigDecimal minValue;
    @Schema(description = Constants.LOAN_TYPE_MAX_VALUE_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_MAX_VALUE)
    private BigDecimal maxValue;
    @Schema(description = Constants.LOAN_TYPE_INTEREST_RATE_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_INTEREST_RATE)
    private BigDecimal interestRate;
    @Schema(description = Constants.LOAN_TYPE_AUTO_VALIDATION_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_AUTO_VALIDATION)
    private Boolean autoValidation;

}
