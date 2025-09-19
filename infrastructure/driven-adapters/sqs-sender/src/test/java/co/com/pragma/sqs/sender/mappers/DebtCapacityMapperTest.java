package co.com.pragma.sqs.sender.mappers;

import co.com.pragma.model.sqs.DebtCapacity;
import co.com.pragma.sqs.sender.dto.DebtCapacitySqsMessage;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DebtCapacityMapperTest {

    private final co.com.pragma.sqs.sender.mapper.DebtCapacityMapper mapper = Mappers.getMapper(co.com.pragma.sqs.sender.mapper.DebtCapacityMapper.class);

    @Test
    void shouldMapDebtCapacityToDto() {
        // Arrange
        DebtCapacity domain = DebtCapacity.builder()
                .solicitudeId(100)
                .baseSalary(new BigDecimal("5000000"))
                .value(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("1.5"))
                .currentTotalMonthlyFee(new BigDecimal("250000.00"))
                .deadline(24)
                .token("fake-jwt-token")
                .build();

        // Act
        DebtCapacitySqsMessage dto = mapper.toSqsMessage(domain);

        // Assert
        assertNotNull(dto);
        assertEquals(domain.getSolicitudeId(), dto.getSolicitudeId());
        assertEquals(0, domain.getBaseSalary().compareTo(dto.getBaseSalary()));
        assertEquals(0, domain.getValue().compareTo(dto.getValue()));
        assertEquals(0, domain.getInterestRate().compareTo(dto.getInterestRate()));
        assertEquals(0, domain.getCurrentTotalMonthlyFee().compareTo(dto.getCurrentTotalMonthlyFee()));
        assertEquals(domain.getDeadline(), dto.getDeadline());
        assertEquals(domain.getToken(), dto.getToken());
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toSqsMessage(null));
    }
}
