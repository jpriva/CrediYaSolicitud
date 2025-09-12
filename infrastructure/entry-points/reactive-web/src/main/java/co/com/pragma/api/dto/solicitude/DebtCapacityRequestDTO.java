package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.ApiConstants.StateChangeDocs;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class DebtCapacityRequestDTO {

    @NotBlank
    @Schema(
            description = StateChangeDocs.ID_SOLICITUDE_DESC,
            example = StateChangeDocs.EXAMPLE_ID_SOLICITUDE
    )
    private String idLoanApplication;

    @NotBlank
    @Schema(
            description = StateChangeDocs.UPDATE_STATE_DESC,
            example = StateChangeDocs.EXAMPLE_UPDATE_STATE,
            allowableValues = {StateChangeDocs.APPROVED_STATE, StateChangeDocs.REJECTED_STATE, StateChangeDocs.MANUAL_STATE}
    )
    private String state;
}