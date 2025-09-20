package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.ApiConstants.Schemas;
import co.com.pragma.api.constants.ApiConstants.SolicitudeDocs;
import co.com.pragma.api.constants.ApiConstants.LoanTypeDocs;
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
@Schema(name = Schemas.SOLICITUDE_REQUEST_SCHEMA_NAME, description = Schemas.SOLICITUDE_REQUEST_SCHEMA_DESCRIPTION)
public class SolicitudeRequestDTO {
    @NotNull(message = SolicitudeDocs.VALIDATION_VALUE_NOT_NULL)
    @DecimalMin(value = "1.00", message = SolicitudeDocs.VALIDATION_VALUE_MIN)
    @DecimalMax(value = "999999999999999999.99", message = SolicitudeDocs.VALIDATION_VALUE_MAX)
    @Schema(description = SolicitudeDocs.SOLICITUDE_REQUEST_VALUE_DESCRIPTION, example = SolicitudeDocs.EXAMPLE_SOLICITUDE_VALUE)
    private BigDecimal value;

    @NotNull(message = SolicitudeDocs.VALIDATION_DEADLINE_NOT_NULL)
    @Min(value = 1, message = SolicitudeDocs.VALIDATION_DEADLINE_MIN)
    @Max(value = 360, message = SolicitudeDocs.VALIDATION_DEADLINE_MAX)
    @Schema(description = SolicitudeDocs.SOLICITUDE_REQUEST_DEADLINE_DESCRIPTION, example = SolicitudeDocs.EXAMPLE_SOLICITUDE_DEADLINE)
    private Integer deadline;

    @NotBlank(message = SolicitudeDocs.VALIDATION_ID_NUMBER_NOT_BLANK)
    @Schema(description = SolicitudeDocs.SOLICITUDE_REQUEST_ID_NUMBER_DESCRIPTION, example = SolicitudeDocs.EXAMPLE_ID_NUMBER)
    private String idNumber;

    @NotNull(message = LoanTypeDocs.VALIDATION_LOAN_TYPE_ID_NOT_NULL)
    @Schema(description = LoanTypeDocs.LOAN_TYPE_REQUEST_ID_DESCRIPTION, example = LoanTypeDocs.EXAMPLE_LOAN_TYPE_ID)
    private Integer loanTypeId;
}
