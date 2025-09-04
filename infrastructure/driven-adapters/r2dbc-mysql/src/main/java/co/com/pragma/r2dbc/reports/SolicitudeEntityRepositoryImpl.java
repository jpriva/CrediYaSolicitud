package co.com.pragma.r2dbc.reports;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.r2dbc.reports.utils.SolicitudeReportUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class SolicitudeEntityRepositoryImpl implements SolicitudeEntityRepositoryCustom {

    private final DatabaseClient databaseClient;
    private final LoggerPort logger;

    @Override
    public Flux<SolicitudeReport> findSolicitudeReport(SolicitudeReportFilter filter) {

        String baseQuery = SolicitudeReportUtils.baseQuery(SolicitudeReportUtils.queryFields());

        Map<String, Object> params = new HashMap<>();
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

        String finalQuery = baseQuery +
                whereClause +
                SolicitudeReportUtils.buildOrderBy(filter) +
                " LIMIT :limit OFFSET :offset";

        params.put("limit", filter.getSize());
        params.put("offset", filter.getPage() * filter.getSize());

        logger.debug("Report Query: {}", finalQuery);
        logger.debug("Query Params: {}", params.toString());

        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(finalQuery);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            executeSpec = executeSpec.bind(entry.getKey(), entry.getValue());
        }

        return executeSpec
                .map((row, rowMetadata) ->
                        SolicitudeReport.builder()
                                .solicitudeId(row.get("solicitudeId", Integer.class))
                                .value(row.get("value", BigDecimal.class))
                                .deadline(row.get("deadline", Integer.class))
                                .clientEmail(row.get("email", String.class))
                                .stateName(row.get("stateName", String.class))
                                .loanTypeName(row.get("loanTypeName", String.class))
                                .interestRate(row.get("interestRate", BigDecimal.class))
                                .totalMonthlyPay(row.get("totalMonthlyValueApproved", BigDecimal.class))
                                .build()
                ).all();
    }

    @Override
    public Mono<Long> countSolicitudeReport(SolicitudeReportFilter filter) {
        String baseQuery = SolicitudeReportUtils.baseQuery("COUNT(s.id_solicitud)");
        Map<String, Object> params = new HashMap<>();
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        SolicitudeReportUtils.addSolicitudeReportFilters(whereClause, params, filter);

        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(baseQuery + whereClause);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            executeSpec = executeSpec.bind(entry.getKey(), entry.getValue());
        }
        return executeSpec.map(row -> row.get(0, Long.class)).one().defaultIfEmpty(0L);
    }
}
