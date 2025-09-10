package co.com.pragma.api.dto.reports;

import co.com.pragma.api.constants.ApiConstants;
import co.com.pragma.api.dto.page.PaginatedResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = ApiConstants.Schemas.PAGINATED_REPORT_SCHEMA_NAME,
        description = ApiConstants.ReportDocs.PAGINATED_REPORT_SCHEMA_DESCRIPTION
)
public class SolicitudeReportPaginatedResponseDTO extends PaginatedResponseDTO<SolicitudeReportResponseDTO> {
}