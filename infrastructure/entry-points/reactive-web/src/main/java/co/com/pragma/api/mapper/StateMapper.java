package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanTypeRequestDTO;
import co.com.pragma.api.dto.LoanTypeResponseDTO;
import co.com.pragma.api.dto.StateResponseDTO;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.state.State;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StateMapper {

    StateResponseDTO toResponseDto(State state);

}