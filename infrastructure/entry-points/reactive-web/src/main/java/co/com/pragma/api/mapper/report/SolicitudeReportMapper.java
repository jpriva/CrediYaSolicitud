package co.com.pragma.api.mapper.report;

import co.com.pragma.api.dto.reports.SolicitudeReportRequestDTO;
import co.com.pragma.api.dto.reports.SolicitudeReportResponseDTO;
import co.com.pragma.model.solicitude.reports.SolicitudeReport;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudeReportMapper {

    @Mapping(source = "state", target = "stateName")
    SolicitudeReportFilter toDomain(SolicitudeReportRequestDTO request);

    SolicitudeReportResponseDTO toResponseDto(SolicitudeReport solicitudeReport);
}
