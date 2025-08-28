package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PersistenceSolicitudeMapper.class)
public interface PersistenceSolicitudeMapper {
    @Mapping(target = "loanType", ignore = true)
    @Mapping(target = "state", ignore = true)
    Solicitude toDomain(SolicitudeEntity solicitudeEntity);

    @Mapping(source = "loanType.loanTypeId", target = "loanTypeId")
    @Mapping(source = "state.stateId", target = "stateId")
    SolicitudeEntity toEntity(Solicitude solicitude);
}
