package co.com.pragma.webclient.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {
    private String name;
    private String lastName;
    private String email;
    private BigDecimal baseSalary;
}
