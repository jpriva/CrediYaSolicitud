package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Loan Application Response", description = "Loan Application Data.")
public class SolicitudeResponseDTO {

    @Schema(description = "Loan application's unique identifier.", example = "123")
    private Integer solicitudeId;
    @Schema(description = "Loan application's value.", example = "123456.78")
    private BigDecimal value;
    @Schema(description = "Loan application's deadline in months.", example = "12")
    private Integer deadline;
    @Schema(description = "User's email", example = "john.doe@example.com")
    private String email;
    @Schema(description = "State of the Loan application.")
    private StateResponseDTO state;
    @Schema(description = "Loan Type of the Loan application.")
    private LoanTypeResponseDTO loanType;
}
