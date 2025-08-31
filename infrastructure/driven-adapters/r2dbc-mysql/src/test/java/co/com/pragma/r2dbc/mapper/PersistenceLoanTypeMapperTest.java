package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceLoanTypeMapperTest {

    private PersistenceLoanTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PersistenceLoanTypeMapper.class);
    }

    @Test
    void shouldMapEntityToDomain() {
        LoanTypeEntity entity = new LoanTypeEntity();
        entity.setLoanTypeId(1);
        entity.setName("CREDI-TEST");
        entity.setMinValue(new BigDecimal("1000.00"));
        entity.setMaxValue(new BigDecimal("10000.00"));
        entity.setInterestRate(new BigDecimal("5.50"));
        entity.setAutoValidation(true);

        LoanType domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getLoanTypeId()).isEqualTo(entity.getLoanTypeId());
        assertThat(domain.getName()).isEqualTo(entity.getName());
        assertThat(domain.getMinValue()).isEqualTo(entity.getMinValue());
        assertThat(domain.getMaxValue()).isEqualTo(entity.getMaxValue());
        assertThat(domain.getInterestRate()).isEqualTo(entity.getInterestRate());
        assertThat(domain.getAutoValidation()).isEqualTo(entity.getAutoValidation());
    }

    @Test
    void shouldMapDomainToEntity() {
        // Arrange
        LoanType domain = LoanType.builder()
                .loanTypeId(1)
                .name("CREDI-TEST")
                .minValue(new BigDecimal("1000.00"))
                .maxValue(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("5.50"))
                .autoValidation(true)
                .build();

        // Act
        LoanTypeEntity entity = mapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getLoanTypeId()).isEqualTo(domain.getLoanTypeId());
        assertThat(entity.getName()).isEqualTo(domain.getName());
        assertThat(entity.getMinValue()).isEqualTo(domain.getMinValue());
        assertThat(entity.getMaxValue()).isEqualTo(domain.getMaxValue());
        assertThat(entity.getInterestRate()).isEqualTo(domain.getInterestRate());
        assertThat(entity.getAutoValidation()).isEqualTo(domain.getAutoValidation());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        // Act
        LoanType domain = mapper.toDomain(null);

        // Assert
        assertThat(domain).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        // Act
        LoanTypeEntity entity = mapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
    }
}