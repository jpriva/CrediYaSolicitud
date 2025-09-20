package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.solicitude.StateResponseDTO;
import co.com.pragma.api.mapper.solicitude.StateMapper;
import co.com.pragma.model.state.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class StateMapperTest {

    private StateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(StateMapper.class);
    }

    @Test
    void shouldMapDomainToResponseDto() {
        State domain = State.builder()
                .stateId(1)
                .name("APROBADO")
                .description("La solicitud ha sido aprobada.")
                .build();

        StateResponseDTO dto = mapper.toResponseDto(domain);

        assertThat(dto).isNotNull();
        assertThat(dto.getStateId()).isEqualTo(domain.getStateId());
        assertThat(dto.getName()).isEqualTo(domain.getName());
        assertThat(dto.getDescription()).isEqualTo(domain.getDescription());
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        StateResponseDTO dto = mapper.toResponseDto(null);

        assertThat(dto).isNull();
    }
}