package co.com.pragma.api.mapper.page;

import co.com.pragma.api.dto.page.PaginatedResponseDTO;
import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
import co.com.pragma.model.page.PaginatedData;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PageMapper {
    PaginatedResponseDTO<SolicitudeReportResponseDTO> toDto(PaginatedData<SolicitudeReport> domain);
}