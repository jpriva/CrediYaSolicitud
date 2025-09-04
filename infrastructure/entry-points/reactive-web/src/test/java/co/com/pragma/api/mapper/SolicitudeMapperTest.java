package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.solicitude.SolicitudeRequestDTO;
import co.com.pragma.api.dto.solicitude.SolicitudeResponseDTO;
import co.com.pragma.api.mapper.solicitude.LoanTypeMapperImpl;
import co.com.pragma.api.mapper.solicitude.SolicitudeMapper;
import co.com.pragma.api.mapper.solicitude.SolicitudeMapperImpl;
import co.com.pragma.api.mapper.solicitude.StateMapperImpl;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.state.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        SolicitudeMapperImpl.class,
        LoanTypeMapperImpl.class,
        StateMapperImpl.class
})
class SolicitudeMapperTest {

    @Autowired
    private SolicitudeMapper mapper;

    @Test
    void shouldMapRequestDtoToDomain() {
        SolicitudeRequestDTO dto = SolicitudeRequestDTO.builder()
                .value(new BigDecimal("15000.00"))
                .deadline(36)
                .idNumber("12345")
                .loanTypeId(5)
                .build();

        Solicitude domain = mapper.toDomain(dto);

        assertThat(domain).isNotNull();
        assertThat(domain.getValue()).isEqualTo(dto.getValue());
        assertThat(domain.getDeadline()).isEqualTo(dto.getDeadline());

        assertThat(domain.getSolicitudeId()).isNull();
        assertThat(domain.getState()).isNull();

        assertThat(domain.getLoanType()).isNotNull();
        assertThat(domain.getLoanType().getLoanTypeId()).isEqualTo(dto.getLoanTypeId());
    }

    @Test
    void shouldMapDomainToResponseDto() {
        Solicitude domain = Solicitude.builder()
                .solicitudeId(101)
                .value(new BigDecimal("15000.00"))
                .deadline(36)
                .email("test@example.com")
                .loanType(LoanType.builder()
                        .loanTypeId(5)
                        .name("CREDI-TEST")
                        .build())
                .state(State.builder()
                        .stateId(2)
                        .name("APROBADO")
                        .build())
                .build();

        SolicitudeResponseDTO dto = mapper.toResponseDto(domain);

        assertThat(dto).isNotNull();
        assertThat(dto.getSolicitudeId()).isEqualTo(domain.getSolicitudeId());
        assertThat(dto.getValue()).isEqualTo(domain.getValue());
        assertThat(dto.getDeadline()).isEqualTo(domain.getDeadline());
        assertThat(dto.getEmail()).isEqualTo(domain.getEmail());

        assertThat(dto.getLoanType()).isNotNull();
        assertThat(dto.getLoanType().getLoanTypeId()).isEqualTo(domain.getLoanType().getLoanTypeId());
        assertThat(dto.getLoanType().getName()).isEqualTo(domain.getLoanType().getName());

        assertThat(dto.getState()).isNotNull();
        assertThat(dto.getState().getStateId()).isEqualTo(domain.getState().getStateId());
        assertThat(dto.getState().getName()).isEqualTo(domain.getState().getName());
    }

    @Test
    void shouldReturnNullWhenRequestDtoIsNull() {
        Solicitude domain = mapper.toDomain(null);

        assertThat(domain).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        SolicitudeResponseDTO dto = mapper.toResponseDto(null);

        assertThat(dto).isNull();
    }
}