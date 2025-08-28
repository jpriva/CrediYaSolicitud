package co.com.pragma.api.dto;

import co.com.pragma.api.constants.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Schema(name = Constants.LOAN_TYPE_REQUEST_SCHEMA_NAME, description = Constants.LOAN_TYPE_REQUEST_SCHEMA_DESCRIPTION, hidden = true)
public class LoanTypeRequestDTO {
    @NotNull(message = Constants.VALIDATION_LOAN_TYPE_ID_NOT_NULL)
    @Schema(description = Constants.LOAN_TYPE_REQUEST_ID_DESCRIPTION, example = Constants.EXAMPLE_LOAN_TYPE_ID)
    private Integer loanTypeId;

}
