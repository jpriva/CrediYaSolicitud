package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.StateResponseDTO;
import co.com.pragma.model.state.State;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StateMapper {

    StateResponseDTO toResponseDto(State state);

}