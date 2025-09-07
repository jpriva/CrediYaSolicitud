package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @NotBlank(message = Constants.VALIDATION_ID_NUMBER_NOT_BLANK)
    @Schema(description = Constants.SOLICITUDE_REQUEST_ID_NUMBER_DESCRIPTION, example = Constants.EXAMPLE_ID_NUMBER)
    private String idNumber;

    @NotNull(message = Constants.VALIDATION_LOAN_TYPE_ID_NOT_NULL)
    @Schema(description = Constants.LOAN_TYPE_REQUEST_ID_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_ID)
    private Integer loanTypeId;
}
