package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.state.State;
import co.com.pragma.r2dbc.entity.StateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceStateMapperTest {

    private PersistenceStateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PersistenceStateMapper.class);
    }

    @Test
    void shouldMapEntityToDomain() {
        // Arrange
        StateEntity entity = new StateEntity();
        entity.setStateId(1);
        entity.setName("PENDIENTE");
        entity.setDescription("Solicitud pendiente de revisión.");

        // Act
        State domain = mapper.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getStateId()).isEqualTo(entity.getStateId());
        assertThat(domain.getName()).isEqualTo(entity.getName());
        assertThat(domain.getDescription()).isEqualTo(entity.getDescription());
    }

    @Test
    void shouldMapDomainToEntity() {
        // Arrange
        State domain = State.builder()
                .stateId(1)
                .name("PENDIENTE")
                .description("Solicitud pendiente de revisión.")
                .build();

        // Act
        StateEntity entity = mapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getStateId()).isEqualTo(domain.getStateId());
        assertThat(entity.getName()).isEqualTo(domain.getName());
        assertThat(entity.getDescription()).isEqualTo(domain.getDescription());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        // Act
        State domain = mapper.toDomain(null);

        // Assert
        assertThat(domain).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        // Act
        StateEntity entity = mapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
    }
}