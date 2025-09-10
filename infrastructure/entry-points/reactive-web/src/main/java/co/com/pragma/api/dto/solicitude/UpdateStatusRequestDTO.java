package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.ApiConstants.StateChangeDocs;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusRequestDTO {
    @NotBlank
    @Schema(
            description = StateChangeDocs.UPDATE_STATE_DESC,
            example = StateChangeDocs.EXAMPLE_UPDATE_STATE,
            allowableValues = {StateChangeDocs.APPROVED_STATE, StateChangeDocs.REJECTED_STATE}
    )
    private String state;
}