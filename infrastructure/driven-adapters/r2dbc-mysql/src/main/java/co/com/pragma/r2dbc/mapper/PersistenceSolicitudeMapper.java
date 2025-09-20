package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PersistenceLoanTypeMapper.class, PersistenceStateMapper.class})
public interface PersistenceSolicitudeMapper {
    @Mapping(target = "loanType.name", ignore = true)
    @Mapping(target = "loanType.minValue", ignore = true)
    @Mapping(target = "loanType.maxValue", ignore = true)
    @Mapping(target = "loanType.interestRate", ignore = true)
    @Mapping(target = "loanType.autoValidation", ignore = true)
    @Mapping(target = "state.name", ignore = true)
    @Mapping(target = "state.description", ignore = true)
    @Mapping(source = "loanTypeId",target = "loanType.loanTypeId")
    @Mapping(source = "stateId",target = "state.stateId")
    Solicitude toDomain(SolicitudeEntity solicitudeEntity);

    @Mapping(source = "loanType.loanTypeId", target = "loanTypeId")
    @Mapping(source = "state.stateId", target = "stateId")
    SolicitudeEntity toEntity(Solicitude solicitude);
}
