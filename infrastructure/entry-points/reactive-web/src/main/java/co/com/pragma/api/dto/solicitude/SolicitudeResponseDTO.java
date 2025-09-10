package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.ApiConstants.Schemas;
import co.com.pragma.api.constants.ApiConstants.SolicitudeDocs;
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
@Schema(name = Schemas.SOLICITUDE_RESPONSE_SCHEMA_NAME, description = Schemas.SOLICITUDE_RESPONSE_SCHEMA_DESCRIPTION)
public class SolicitudeResponseDTO {

    @Schema(description = SolicitudeDocs.SOLICITUDE_ID_DESCRIPTION, example = SolicitudeDocs.EXAMPLE_SOLICITUDE_ID)
    private Integer solicitudeId;
    @Schema(description = SolicitudeDocs.SOLICITUDE_VALUE_DESCRIPTION, example = SolicitudeDocs.EXAMPLE_SOLICITUDE_VALUE)
    private BigDecimal value;
    @Schema(description = SolicitudeDocs.SOLICITUDE_DEADLINE_DESCRIPTION, example = SolicitudeDocs.EXAMPLE_SOLICITUDE_DEADLINE)
    private Integer deadline;
    @Schema(description = SolicitudeDocs.SOLICITUDE_EMAIL_DESCRIPTION, example = SolicitudeDocs.EXAMPLE_EMAIL)
    private String email;
    @Schema(description = SolicitudeDocs.SOLICITUDE_STATE_DESCRIPTION)
    private StateResponseDTO state;
    @Schema(description = SolicitudeDocs.SOLICITUDE_LOAN_TYPE_DESCRIPTION)
    private LoanTypeResponseDTO loanType;
}
