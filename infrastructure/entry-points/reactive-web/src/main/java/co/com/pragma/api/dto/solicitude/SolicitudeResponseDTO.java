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
@EqualsAndHashCode
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = Constants.SOLICITUDE_RESPONSE_SCHEMA_NAME, description = Constants.SOLICITUDE_RESPONSE_SCHEMA_DESCRIPTION)
public class SolicitudeResponseDTO {

    @Schema(description = Constants.SOLICITUDE_ID_DESCRIPTION, example = Constants.EXAMPLE_SOLICITUDE_ID)
    private Integer solicitudeId;
    @Schema(description = Constants.SOLICITUDE_VALUE_DESCRIPTION, example = Constants.EXAMPLE_SOLICITUDE_VALUE)
    private BigDecimal value;
    @Schema(description = Constants.SOLICITUDE_DEADLINE_DESCRIPTION, example = Constants.EXAMPLE_SOLICITUDE_DEADLINE)
    private Integer deadline;
    @Schema(description = Constants.SOLICITUDE_EMAIL_DESCRIPTION, example = Constants.EXAMPLE_EMAIL)
    private String email;
    @Schema(description = Constants.SOLICITUDE_STATE_DESCRIPTION)
    private StateResponseDTO state;
    @Schema(description = Constants.SOLICITUDE_LOAN_TYPE_DESCRIPTION)
    private LoanTypeResponseDTO loanType;
}
