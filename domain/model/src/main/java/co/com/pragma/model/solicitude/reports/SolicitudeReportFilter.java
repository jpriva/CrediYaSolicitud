package co.com.pragma.model.solicitude.reports;

import co.com.pragma.model.page.PageableData;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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
    private List<String> emailsIn;

    private PageableData pageable;

}