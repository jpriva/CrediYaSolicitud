package co.com.pragma.api.dto.reports;

import co.com.pragma.api.dto.page.PaginatedResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PaginatedSolicitudeReportResponse", description = "A paginated list of loan application reports.")
public class SolicitudeReportPaginatedResponseDTO extends PaginatedResponseDTO<SolicitudeReportResponseDTO> {
}