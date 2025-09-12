package co.com.pragma.sqs.sender.mapper;

import co.com.pragma.model.sqs.DebtCapacity;
import co.com.pragma.sqs.sender.dto.DebtCapacitySqsMessage;
import co.com.pragma.sqs.sender.util.MoneySerializer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MoneySerializer.class})
public interface DebtCapacityMapper {
    DebtCapacitySqsMessage toSqsMessage(DebtCapacity debtCapacity);
}
