package co.com.pragma.model.solicitude.reports;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SolicitudeReport {
    private Integer solicitudeId;
    private BigDecimal value;
    private Integer deadline;
    private String clientEmail;
    private String clientName;
    private String clientIdentification;
    private String loanTypeName;
    private BigDecimal interestRate;
    private String stateName;
    private BigDecimal baseSalary;
    private BigDecimal totalMonthlyPay;
}
