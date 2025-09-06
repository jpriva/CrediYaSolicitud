package co.com.pragma.r2dbc.reports.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.StringJoiner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitudeReportUtils {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Fields {
        public static final String SOLICITUDE_ID = "s.id_solicitud AS solicitudeId";
        public static final String VALUE = "s.monto AS value";
        public static final String DEADLINE = "s.plazo AS deadline";
        public static final String EMAIL = "s.email AS email";
        public static final String STATE_NAME = "e.nombre AS stateName";
        public static final String LOAN_TYPE_NAME = "tp.nombre AS loanTypeName";
        public static final String INTEREST_RATE = "tp.tasa_interes AS interestRate";
        public static final String TOTAL_MONTHLY_VALUE = """
                (
                    SELECT COALESCE(SUM(
                        sx.monto *
                        ( (tpx.tasa_interes/100) * POWER(1 + (tpx.tasa_interes/100), sx.plazo) ) /
                        ( POWER(1 + (tpx.tasa_interes/100), sx.plazo ) - 1 )
                     ), 0)
                     FROM solicitud sx
                     JOIN tipo_prestamo tpx ON tpx.id_tipo_prestamo = sx.id_tipo_prestamo
                     JOIN estados ex ON ex.id_estado = sx.id_estado
                     WHERE sx.email = s.email
                     AND ex.nombre = :approvedStateForSubquery
                ) AS totalMonthlyValueApproved""";
    }

    public static String queryFields() {
        StringJoiner fieldsJoiner = new StringJoiner(", ")
                .add(Fields.SOLICITUDE_ID)
                .add(Fields.VALUE)
                .add(Fields.DEADLINE)
                .add(Fields.EMAIL)
                .add(Fields.STATE_NAME)
                .add(Fields.LOAN_TYPE_NAME)
                .add(Fields.INTEREST_RATE)

                //SUM( V * [I*(1+I)^D] / [(1+I)^(D-1)] )
                .add(Fields.TOTAL_MONTHLY_VALUE);

        return fieldsJoiner.toString();
    }

    public static String baseQuery(String fields) {
        if (fields == null) return "";
        StringJoiner baseQueryJoiner = new StringJoiner(" ")
                .add("SELECT")
                .add(fields.trim())
                .add("FROM solicitud s")
                .add("JOIN estados e ON e.id_estado = s.id_estado")
                .add("JOIN tipo_prestamo tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo");
        return baseQueryJoiner.toString();

    }

    public static void addSolicitudeReportFilters(StringBuilder whereClause, Map<String, Object> params, SolicitudeReportFilter filter) {

        if (filter.getStateName() == null || filter.getStateName().isBlank()) {
            whereClause.append(" AND e.nombre != :defaultApprovedState");
            params.put("defaultApprovedState", DefaultValues.APPROVED_STATE);
        }

        if (filter.getEmailsIn() != null && !filter.getEmailsIn().isEmpty()) {
            whereClause.append(" AND s.email IN (:emailsIn)");
            params.put("emailsIn", filter.getEmailsIn());
        } else {
            addFilter(whereClause, params, "s.email", filter.getClientEmail(), "clientEmail", true);
        }
        addFilter(whereClause, params, "tp.nombre", filter.getLoanTypeName(), "loanTypeName", true);
        addFilter(whereClause, params, "e.nombre", filter.getStateName(), "stateName", false);

        if (filter.getMinValue() != null) {
            whereClause.append(" AND s.monto >= :minValue");
            params.put("minValue", filter.getMinValue());
        }
        if (filter.getMaxValue() != null) {
            whereClause.append(" AND s.monto <= :maxValue");
            params.put("maxValue", filter.getMaxValue());
        }
    }

    public static String buildOrderBy(SolicitudeReportFilter filter) {
        if (filter.getPageable().getSortBy() != null && !filter.getPageable().getSortBy().isBlank()) {
            String sortField = switch (filter.getPageable().getSortBy()) {
                case "monto" -> "s.monto";
                case "email" -> "s.email";
                case "estado" -> "e.nomebre";
                case "tipo_prestamo" -> "tp.nombre";
                default -> "s.id_solicitud";
            };
            String direction = !"ASC".equalsIgnoreCase(filter.getPageable().getSortDirection()) ? "DESC" : "ASC";
            return " ORDER BY " + sortField + " " + direction;
        }
        return " ORDER BY s.id_solicitud DESC";
    }

    private static void addFilter(StringBuilder whereClause,
                                  Map<String, Object> params,
                                  String dbField,
                                  String filterValue,
                                  String paramName,
                                  boolean useLike) {
        if (filterValue != null && !filterValue.isBlank()) {
            if (useLike) {
                whereClause.append(" AND ").append(dbField).append(" LIKE :").append(paramName);
                params.put(paramName, "%" + filterValue + "%");
            } else {
                whereClause.append(" AND ").append(dbField).append(" = :").append(paramName);
                params.put(paramName, filterValue);
            }
        }
    }
}
