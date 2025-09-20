package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.ApiConstants.LoanTypeDocs;
import co.com.pragma.api.constants.ApiConstants.Schemas;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = Schemas.LOAN_TYPE_SCHEMA_NAME, description = Schemas.LOAN_TYPE_SCHEMA_DESCRIPTION)
@Builder(toBuilder = true)
public class LoanTypeResponseDTO {
    @Schema(description = LoanTypeDocs.LOAN_TYPE_ID_DESCRIPTION, example = LoanTypeDocs.EXAMPLE_LOAN_TYPE_ID)
    private Integer loanTypeId;
    @Schema(description = LoanTypeDocs.LOAN_TYPE_NAME_DESCRIPTION, example = LoanTypeDocs.EXAMPLE_LOAN_TYPE_NAME)
    private String name;
    @Schema(description = LoanTypeDocs.LOAN_TYPE_MIN_VALUE_DESCRIPTION, example = LoanTypeDocs.EXAMPLE_LOAN_TYPE_MIN_VALUE)
    private BigDecimal minValue;
    @Schema(description = LoanTypeDocs.LOAN_TYPE_MAX_VALUE_DESCRIPTION, example = LoanTypeDocs.EXAMPLE_LOAN_TYPE_MAX_VALUE)
    private BigDecimal maxValue;
    @Schema(description = LoanTypeDocs.LOAN_TYPE_INTEREST_RATE_DESCRIPTION, example = LoanTypeDocs.EXAMPLE_LOAN_TYPE_INTEREST_RATE)
    private BigDecimal interestRate;
    @Schema(description = LoanTypeDocs.LOAN_TYPE_AUTO_VALIDATION_DESCRIPTION, example = LoanTypeDocs.EXAMPLE_LOAN_TYPE_AUTO_VALIDATION)
    private Boolean autoValidation;

}
