package co.com.pragma.model.user;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserProjection{
        String email;
        String name;
        String idNumber;
        BigDecimal baseSalary;
}