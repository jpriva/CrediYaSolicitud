package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Loan Type Response", description = "Loan Type Data.")
@Builder(toBuilder = true)
public class LoanTypeResponseDTO {
    @Schema(description = "Loan type id", example = "1")
    private Integer loanTypeId;
    @Schema(description = "Loan type name", example = "CREDI-HOGAR")
    private String name;
    @Schema(description = "Loan type minimum value", example = "50000000.00")
    private BigDecimal minValue;
    @Schema(description = "Loan type maximum value", example = "800000000.00")
    private BigDecimal maxValue;
    @Schema(description = "Loan type interest rate", example = "7.20")
    private BigDecimal interestRate;
    @Schema(description = "Loan type autovalidation", example = "0")
    private Boolean autoValidation;

}
