package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Loan Application Request", description = "Loan Application Data.")
public class SolicitudeRequestDTO {
    @NotBlank(message = "Value can't be empty")
    @DecimalMin(value = "1.00", message = "Minimum value depends on loan type")
    @DecimalMax(value = "999999999999999999.99", message = "Maximum value depends on loan type")
    @Schema(description = "Loan application's value.", example = "123456.78")
    private BigDecimal value;

    @NotBlank(message = "Deadline can't be empty")
    @DecimalMin(value = "1", message = "Minimum value most be at least 1")
    @DecimalMax(value = "360", message = "Maximum value most be at least 360 equivalent to 30 years")
    @Schema(description = "Loan application's deadline in months.", example = "12")
    private Integer deadline;

    @NotBlank(message = "Email can't be empty")
    @Schema(description = "User's email", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Loan Type can't be empty")
    @Schema(description = "Loan Type.")
    private LoanTypeRequestDTO loanType;
}
