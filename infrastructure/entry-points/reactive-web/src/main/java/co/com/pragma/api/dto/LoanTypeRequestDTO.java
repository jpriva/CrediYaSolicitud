package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Schema(name = "Loan Type", description = "Loan Type Data.")
public class LoanTypeRequestDTO {
    @NotBlank(message = "Loan type id can't be empty")
    @Schema(description = "Loan type id", example = "1")
    private Integer loanTypeId;

}
