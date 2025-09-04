package co.com.pragma.api.mapper.report;

import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterMapper {
    public static SolicitudeReportFilter toFilter(MultiValueMap<String, String> params) {

        SolicitudeReportFilter.SolicitudeReportFilterBuilder builder = SolicitudeReportFilter.builder();

        getParam(params, "clientEmail").ifPresent(builder::clientEmail);
        getParam(params, "clientName").ifPresent(builder::clientName);
        getParam(params, "clientIdentification").ifPresent(builder::clientIdNumber);
        getParam(params, "loanType").ifPresent(builder::loanTypeName);
        getParam(params, "state").ifPresent(builder::stateName);

        getBigDecimalParam(params, "minValue").ifPresent(builder::minValue);
        getBigDecimalParam(params, "maxValue").ifPresent(builder::maxValue);
        getBigDecimalParam(params, "minBaseSalary").ifPresent(builder::minBaseSalary);
        getBigDecimalParam(params, "maxBaseSalary").ifPresent(builder::maxBaseSalary);

        builder.page(getIntParam(params, "page").orElse(0));
        builder.size(getIntParam(params, "size").orElse(10));

        getParam(params, "sortBy").ifPresent(builder::sortBy);
        getParam(params, "sortDirection").ifPresent(builder::sortDirection);

        return builder
                .build();
    }

    private static Optional<String> getParam(MultiValueMap<String, String> params, String key) {
        return Optional.ofNullable(params.getFirst(key)).filter(s -> !s.isBlank());
    }

    private static Optional<BigDecimal> getBigDecimalParam(MultiValueMap<String, String> params, String key) {
        return getParam(params, key).flatMap(s -> {
            try {
                return Optional.of(new BigDecimal(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
    }

    private static Optional<Integer> getIntParam(MultiValueMap<String, String> params, String key) {
        return getParam(params, key).flatMap(s -> {
            try {
                return Optional.of(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
    }
}
