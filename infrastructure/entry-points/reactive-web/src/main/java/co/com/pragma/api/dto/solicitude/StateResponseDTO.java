package co.com.pragma.api.dto.solicitude;

import co.com.pragma.api.constants.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = Constants.STATE_SCHEMA_NAME, description = Constants.STATE_SCHEMA_DESCRIPTION)
public class StateResponseDTO {
    @Schema(description = Constants.STATE_ID_DESCRIPTION, example = Constants.EXAMPLE_STATE_ID)
    private Integer stateId;
    @Schema(description = Constants.STATE_NAME_DESCRIPTION, example = Constants.EXAMPLE_STATE_NAME)
    private String name;
    @Schema(description = Constants.STATE_DESCRIPTION_DESCRIPTION, example = Constants.EXAMPLE_STATE_DESCRIPTION)
    private String description;
}
