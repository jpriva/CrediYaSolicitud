package co.com.pragma.r2dbc.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.r2dbc.reports.utils.SolicitudeReportUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudeReportUtilsTest {

    @Test
    void queryFields_shouldContainAllExpectedFieldsAndAliases() {
        String fields = SolicitudeReportUtils.queryFields();

        assertThat(fields)
                .isNotBlank()
                .contains("s.id_solicitud AS solicitudeId")
                .contains("s.monto AS value")
                .contains("s.plazo AS deadline")
                .contains("s.email AS email")
                .contains("e.nombre AS stateName")
                .contains("tp.nombre AS loanTypeName")
                .contains("tp.tasa_interes AS interestRate")
                .contains("AS totalMonthlyValueApproved");
    }

    @Test
    void baseQuery_shouldConstructCorrectBaseStructure() {
        String fields = "s.id_solicitud, s.monto";
        String query = SolicitudeReportUtils.baseQuery(fields);

        assertThat(query)
                .startsWith("SELECT s.id_solicitud, s.monto FROM")
                .contains("FROM solicitud s")
                .contains("JOIN estados e ON e.id_estado = s.id_estado")
                .contains("JOIN tipo_prestamo tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo");
    }

    @Test
    void baseQuery_withNullFields_shouldReturnEmptyString() {
        String query = SolicitudeReportUtils.baseQuery(null);
        assertThat(query).isEmpty();
    }

    @Nested
    class AddSolicitudeReportFiltersTests {

        private StringBuilder whereClause;
        private Map<String, Object> params;

        @BeforeEach
        void setUp() {
            whereClause = new StringBuilder();
            params = new HashMap<>();
        }

        @Test
        void withNoFilters_shouldAddDefaultFilterToExcludeApprovedState() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString()).hasToString(" AND e.nombre != :defaultApprovedState");
            assertThat(params).containsEntry("defaultApprovedState", DefaultValues.APPROVED_STATE);
        }

        @Test
        void withStateFilter_shouldNotAddDefaultExcludeFilter() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().stateName("RECHAZADO").build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString()).contains(" AND e.nombre = :stateName");
            assertThat(whereClause.toString()).doesNotContain("e.nombre != :defaultApprovedState");
            assertThat(params).containsEntry("stateName", "RECHAZADO");
        }

        @Test
        void withEmailsInFilter_shouldUseInClauseAndIgnoreClientEmail() {
            List<String> emails = List.of("test1@test.com", "test2@test.com");
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .emailsIn(emails)
                    .clientEmail("ignored@test.com")
                    .build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString()).contains(" AND s.email IN (:emailsIn)");
            assertThat(whereClause.toString()).doesNotContain("s.email LIKE :clientEmail");
            assertThat(params).containsEntry("emailsIn", emails);
        }

        @Test
        void withClientEmailFilter_shouldUseLikeClause() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().clientEmail("test@").build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString()).contains(" AND s.email LIKE :clientEmail");
            assertThat(params).containsEntry("clientEmail", "%test@%");
        }

        @Test
        void withLoanTypeFilter_shouldUseLikeClause() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().loanTypeName("Personal").build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString()).contains(" AND tp.nombre LIKE :loanTypeName");
            assertThat(params).containsEntry("loanTypeName", "%Personal%");
        }

        @Test
        void withMinValueFilter_shouldAddGreaterOrEqualCondition() {
            BigDecimal minValue = new BigDecimal("1000.00");
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().minValue(minValue).build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString()).contains(" AND s.monto >= :minValue");
            assertThat(params).containsEntry("minValue", minValue);
        }

        @Test
        void withMaxValueFilter_shouldAddLessOrEqualCondition() {
            BigDecimal maxValue = new BigDecimal("5000.00");
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().maxValue(maxValue).build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString()).contains(" AND s.monto <= :maxValue");
            assertThat(params).containsEntry("maxValue", maxValue);
        }

        @Test
        void withMultipleFilters_shouldCombineConditions() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .stateName("PENDIENTE")
                    .minValue(new BigDecimal("500"))
                    .build();

            SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

            assertThat(whereClause.toString())
                    .contains(" AND e.nombre = :stateName")
                    .contains(" AND s.monto >= :minValue")
                    .doesNotContain("e.nombre != :defaultApprovedState");
            assertThat(params).containsKeys("stateName", "minValue");
        }
    }

    @Nested
    class BuildOrderByTests {

        @Test
        void withSortByValue_shouldOrderByMonto() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .sortBy("value")
                    .sortDirection("ASC")
                    .build();

            String orderBy = SolicitudeReportUtils.buildOrderBy(filter);

            assertThat(orderBy).isEqualTo(" ORDER BY s.monto ASC");
        }

        @Test
        void withSortByEmail_andDescDirection_shouldOrderByEmailDesc() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .sortBy("email")
                    .sortDirection("DESC")
                    .build();

            String orderBy = SolicitudeReportUtils.buildOrderBy(filter);

            assertThat(orderBy).isEqualTo(" ORDER BY s.email DESC");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "state, ' ORDER BY e.nombre DESC'",
                "invalidField, ' ORDER BY s.id_solicitud DESC'",
                "null, ' ORDER BY s.id_solicitud DESC'"
        }, nullValues = "null")
        void buildOrderBy_shouldHandleDifferentSortByInputs(String sortBy, String expectedClause) {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder()
                    .sortBy(sortBy)
                    .build();

            String orderBy = SolicitudeReportUtils.buildOrderBy(filter);

            assertThat(orderBy).isEqualTo(expectedClause);
        }

        @Test
        void withBlankSortBy_shouldUseDefaultSort() {
            SolicitudeReportFilter filter = SolicitudeReportFilter.builder().sortBy(" ").build();

            String orderBy = SolicitudeReportUtils.buildOrderBy(filter);

            assertThat(orderBy).isEqualTo(" ORDER BY s.id_solicitud DESC");
        }
    }
}
