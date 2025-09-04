package co.com.pragma.api.mapper.report;

import co.com.pragma.api.constants.ApiConstants.FilterParams;
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

        getParam(params, FilterParams.CLIENT_EMAIL).ifPresent(builder::clientEmail);
        getParam(params, FilterParams.CLIENT_NAME).ifPresent(builder::clientName);
        getParam(params, FilterParams.CLIENT_IDENTIFICATION).ifPresent(builder::clientIdNumber);
        getParam(params, FilterParams.LOAN_TYPE).ifPresent(builder::loanTypeName);
        getParam(params, FilterParams.STATE).ifPresent(builder::stateName);

        getBigDecimalParam(params, FilterParams.MIN_VALUE).ifPresent(builder::minValue);
        getBigDecimalParam(params, FilterParams.MAX_VALUE).ifPresent(builder::maxValue);
        getBigDecimalParam(params, FilterParams.MIN_BASE_SALARY).ifPresent(builder::minBaseSalary);
        getBigDecimalParam(params, FilterParams.MAX_BASE_SALARY).ifPresent(builder::maxBaseSalary);

        builder.page(getIntParam(params, FilterParams.PAGE).orElse(FilterParams.DEFAULT_PAGE));
        builder.size(getIntParam(params, FilterParams.SIZE).orElse(FilterParams.DEFAULT_SIZE));

        getParam(params, FilterParams.SORT_BY).ifPresent(builder::sortBy);
        getParam(params, FilterParams.SORT_DIRECTION).ifPresent(builder::sortDirection);

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
