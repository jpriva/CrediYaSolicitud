package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.SolicitudeRequestDTO;
import co.com.pragma.api.dto.SolicitudeResponseDTO;
import co.com.pragma.model.solicitude.Solicitude;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {LoanTypeMapper.class, StateMapper.class})
public interface SolicitudeMapper {

    @Mapping(target = "loanType.name", ignore = true)
    @Mapping(target = "loanType.minValue", ignore = true)
    @Mapping(target = "loanType.maxValue", ignore = true)
    @Mapping(target = "loanType.interestRate", ignore = true)
    @Mapping(target = "loanType.autoValidation", ignore = true)
    @Mapping(target = "solicitudeId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(source = "loanTypeId", target = "loanType.loanTypeId")
    Solicitude toDomain(SolicitudeRequestDTO dto);

    SolicitudeResponseDTO toResponseDto(Solicitude solicitude);

}