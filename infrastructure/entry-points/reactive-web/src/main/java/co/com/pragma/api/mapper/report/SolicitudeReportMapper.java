package co.com.pragma.api.mapper.report;

import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitudeReportMapper {

    SolicitudeReportResponseDTO toResponseDto(SolicitudeReport solicitudeReport);
}
