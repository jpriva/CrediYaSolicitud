package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.ApiConstants.Schemas;
import co.com.pragma.api.constants.ApiConstants.StateDocs;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = Schemas.STATE_SCHEMA_NAME, description = Schemas.STATE_SCHEMA_DESCRIPTION)
public class StateResponseDTO {
    @Schema(description = StateDocs.STATE_ID_DESCRIPTION, example = StateDocs.EXAMPLE_STATE_ID)
    private Integer stateId;
    @Schema(description = StateDocs.STATE_NAME_DESCRIPTION, example = StateDocs.EXAMPLE_STATE_NAME)
    private String name;
    @Schema(description = StateDocs.STATE_DESCRIPTION_DESCRIPTION, example = StateDocs.EXAMPLE_STATE_DESCRIPTION)
    private String description;
}
