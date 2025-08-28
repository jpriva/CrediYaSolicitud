package co.com.pragma.api.dto;

import co.com.pragma.model.constants.DefaultValues;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "State", description = "State Data.")
public class StateResponseDTO {
    @Schema(description = "State identifier", example = "1")
    private Integer stateId;
    @Schema(description = "State name", example = DefaultValues.PENDING_STATE)
    private String name;
    @Schema(description = "State description", example = "La solicitud está pendiente de revisión.")
    private String description;
}
