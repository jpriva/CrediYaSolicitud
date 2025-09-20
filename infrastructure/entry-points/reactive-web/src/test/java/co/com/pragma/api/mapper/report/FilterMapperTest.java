package co.com.pragma.api.mapper.report;

import co.com.pragma.api.constants.ApiConstants.FilterParams;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FilterMapperTest {

    @Test
    void toFilter_withAllParams_shouldMapAllFields() {
        MultiValueMap<String, String> params = getStringStringMultiValueMap();

        SolicitudeReportFilter filter = FilterMapper.toFilter(params);

        assertEquals("test@example.com", filter.getClientEmail());
        assertEquals("John Doe", filter.getClientName());
        assertEquals("123456789", filter.getClientIdNumber());
        assertEquals("Personal", filter.getLoanTypeName());
        assertEquals("Approved", filter.getStateName());
        assertEquals(new BigDecimal("1000.00"), filter.getMinValue());
        assertEquals(new BigDecimal("5000.00"), filter.getMaxValue());
        assertEquals(new BigDecimal("2000.00"), filter.getMinBaseSalary());
        assertEquals(new BigDecimal("6000.00"), filter.getMaxBaseSalary());
        assertEquals(1, filter.getPageable().getPage());
        assertEquals(20, filter.getPageable().getSize());
        assertEquals("clientName", filter.getPageable().getSortBy());
        assertEquals("DESC", filter.getPageable().getSortDirection());
    }

    private static MultiValueMap<String, String> getStringStringMultiValueMap() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(FilterParams.CLIENT_EMAIL, "test@example.com");
        params.add(FilterParams.CLIENT_NAME, "John Doe");
        params.add(FilterParams.CLIENT_IDENTIFICATION, "123456789");
        params.add(FilterParams.LOAN_TYPE, "Personal");
        params.add(FilterParams.STATE, "Approved");
        params.add(FilterParams.MIN_VALUE, "1000.00");
        params.add(FilterParams.MAX_VALUE, "5000.00");
        params.add(FilterParams.MIN_BASE_SALARY, "2000.00");
        params.add(FilterParams.MAX_BASE_SALARY, "6000.00");
        params.add(FilterParams.PAGE, "1");
        params.add(FilterParams.SIZE, "20");
        params.add(FilterParams.SORT_BY, "clientName");
        params.add(FilterParams.SORT_DIRECTION, "DESC");
        return params;
    }

    @Test
    void toFilter_withNoParams_shouldUseDefaultsAndNulls() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        SolicitudeReportFilter filter = FilterMapper.toFilter(params);

        assertNull(filter.getClientEmail());
        assertNull(filter.getClientName());
        assertNull(filter.getClientIdNumber());
        assertNull(filter.getLoanTypeName());
        assertNull(filter.getStateName());
        assertNull(filter.getMinValue());
        assertNull(filter.getMaxValue());
        assertNull(filter.getMinBaseSalary());
        assertNull(filter.getMaxBaseSalary());
        assertEquals(FilterParams.DEFAULT_PAGE, filter.getPageable().getPage());
        assertEquals(FilterParams.DEFAULT_SIZE, filter.getPageable().getSize());
        assertNull(filter.getPageable().getSortBy());
        assertNull(filter.getPageable().getSortDirection());
    }

    @Test
    void toFilter_withInvalidNumberParams_shouldIgnoreThem() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(FilterParams.MIN_VALUE, "not-a-number");
        params.add(FilterParams.PAGE, "not-an-integer");

        SolicitudeReportFilter filter = FilterMapper.toFilter(params);

        assertNull(filter.getMinValue());
        assertEquals(FilterParams.DEFAULT_PAGE, filter.getPageable().getPage());
    }

    @Test
    void toFilter_withBlankParams_shouldIgnoreThem() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(FilterParams.CLIENT_NAME, " ");
        params.add(FilterParams.STATE, "");

        SolicitudeReportFilter filter = FilterMapper.toFilter(params);

        assertNull(filter.getClientName());
        assertNull(filter.getStateName());
    }
}

