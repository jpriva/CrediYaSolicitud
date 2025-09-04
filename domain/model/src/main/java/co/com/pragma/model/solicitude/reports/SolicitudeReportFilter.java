package co.com.pragma.model.solicitude.reports;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudeReportFilter {

    private String clientEmail;
    private String clientName;
    private String clientIdNumber;
    private String loanTypeName;
    private String stateName;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private BigDecimal minBaseSalary;
    private BigDecimal maxBaseSalary;

    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;

}