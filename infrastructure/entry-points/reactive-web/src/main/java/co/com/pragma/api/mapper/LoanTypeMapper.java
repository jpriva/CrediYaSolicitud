package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanTypeRequestDTO;
import co.com.pragma.api.dto.LoanTypeResponseDTO;
import co.com.pragma.model.loantype.LoanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanTypeMapper {

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "minValue", ignore = true)
    @Mapping(target = "maxValue", ignore = true)
    @Mapping(target = "interestRate", ignore = true)
    @Mapping(target = "autoValidation", ignore = true)
    LoanType toDomain(LoanTypeRequestDTO dto);

    LoanTypeResponseDTO toResponseDto(LoanType loanType);

}