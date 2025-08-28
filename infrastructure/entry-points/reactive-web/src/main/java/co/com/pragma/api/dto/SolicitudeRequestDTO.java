package co.com.pragma.api.dto;

import co.com.pragma.api.constants.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = Constants.SOLICITUDE_REQUEST_SCHEMA_NAME, description = Constants.SOLICITUDE_REQUEST_SCHEMA_DESCRIPTION)
public class SolicitudeRequestDTO {
    @NotNull(message = Constants.VALIDATION_VALUE_NOT_NULL)
    @DecimalMin(value = "1.00", message = Constants.VALIDATION_VALUE_MIN)
    @DecimalMax(value = "999999999999999999.99", message = Constants.VALIDATION_VALUE_MAX)
    @Schema(description = Constants.SOLICITUDE_REQUEST_VALUE_DESCRIPTION, example = Constants.EXAMPLE_SOLICITUDE_VALUE)
    private BigDecimal value;

    @NotNull(message = Constants.VALIDATION_DEADLINE_NOT_NULL)
    @Min(value = 1, message = Constants.VALIDATION_DEADLINE_MIN)
    @Max(value = 360, message = Constants.VALIDATION_DEADLINE_MAX)
    @Schema(description = Constants.SOLICITUDE_REQUEST_DEADLINE_DESCRIPTION, example = Constants.EXAMPLE_SOLICITUDE_DEADLINE)
    private Integer deadline;

    @NotBlank(message = Constants.VALIDATION_EMAIL_NOT_BLANK)
    @Email(message = Constants.VALIDATION_EMAIL_FORMAT)
    @Schema(description = Constants.SOLICITUDE_REQUEST_EMAIL_DESCRIPTION, example = Constants.EXAMPLE_EMAIL)
    private String email;

    @NotNull(message = Constants.VALIDATION_LOAN_TYPE_NOT_NULL)
    @Valid
    @Schema(description = Constants.SOLICITUDE_REQUEST_LOAN_TYPE_DESCRIPTION)
    private LoanTypeRequestDTO loanType;
}
