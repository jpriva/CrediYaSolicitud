package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.solicitude.LoanTypeResponseDTO;
import co.com.pragma.api.mapper.solicitude.LoanTypeMapper;
import co.com.pragma.model.loantype.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanTypeMapperTest {

    private LoanTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LoanTypeMapper.class);
    }

    @Test
    void shouldMapDomainToResponseDto() {
        LoanType domain = LoanType.builder()
                .loanTypeId(1)
                .name("CREDI-TEST")
                .minValue(new BigDecimal("1000.00"))
                .maxValue(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("5.50"))
                .autoValidation(true)
                .build();

        LoanTypeResponseDTO dto = mapper.toResponseDto(domain);

        assertThat(dto).isNotNull();
        assertThat(dto.getLoanTypeId()).isEqualTo(domain.getLoanTypeId());
        assertThat(dto.getName()).isEqualTo(domain.getName());
        assertThat(dto.getMinValue()).isEqualTo(domain.getMinValue());
        assertThat(dto.getMaxValue()).isEqualTo(domain.getMaxValue());
        assertThat(dto.getInterestRate()).isEqualTo(domain.getInterestRate());
        assertThat(dto.getAutoValidation()).isEqualTo(domain.getAutoValidation());
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        LoanTypeResponseDTO dto = mapper.toResponseDto(null);

        assertThat(dto).isNull();
    }
}