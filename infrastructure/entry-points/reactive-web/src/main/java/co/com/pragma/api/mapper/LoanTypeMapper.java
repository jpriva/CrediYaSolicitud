package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanTypeRequestDTO;
import co.com.pragma.api.dto.LoanTypeResponseDTO;
import co.com.pragma.model.loantype.LoanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanTypeMapper {

    @Mapping(target = "loanType.name", ignore = true)
    @Mapping(target = "loanType.minValue", ignore = true)
    @Mapping(target = "loanType.maxValue", ignore = true)
    @Mapping(target = "loanType.interestRate", ignore = true)
    @Mapping(target = "loanType.autoValidation", ignore = true)
    LoanType toDomain(LoanTypeRequestDTO dto);

    LoanTypeResponseDTO toResponseDto(LoanType loanType);

}