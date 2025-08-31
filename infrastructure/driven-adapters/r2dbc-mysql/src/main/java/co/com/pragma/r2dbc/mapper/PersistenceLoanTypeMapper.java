package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersistenceLoanTypeMapper {
    LoanType toDomain(LoanTypeEntity loanTypeEntity);

    LoanTypeEntity toEntity(LoanType loanType);
}
