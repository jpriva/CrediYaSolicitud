package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.state.State;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceSolicitudeMapperTest {

    private PersistenceSolicitudeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PersistenceSolicitudeMapper.class);
    }

    @Test
    void shouldMapEntityToDomain() {
        SolicitudeEntity entity = new SolicitudeEntity();
        entity.setSolicitudeId(1);
        entity.setValue(new BigDecimal("50000.00"));
        entity.setDeadline(24);
        entity.setEmail("test@example.com");
        entity.setLoanTypeId(2);
        entity.setStateId(3);

        Solicitude domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getSolicitudeId()).isEqualTo(entity.getSolicitudeId());
        assertThat(domain.getValue()).isEqualTo(entity.getValue());
        assertThat(domain.getDeadline()).isEqualTo(entity.getDeadline());
        assertThat(domain.getEmail()).isEqualTo(entity.getEmail());

        assertThat(domain.getLoanType()).isNotNull();
        assertThat(domain.getLoanType().getLoanTypeId()).isEqualTo(entity.getLoanTypeId());
        assertThat(domain.getState()).isNotNull();
        assertThat(domain.getState().getStateId()).isEqualTo(entity.getStateId());
    }

    @Test
    void shouldMapDomainToEntity() {
        Solicitude domain = Solicitude.builder()
                .solicitudeId(1)
                .value(new BigDecimal("50000.00"))
                .deadline(24)
                .email("test@example.com")
                .loanType(LoanType.builder().loanTypeId(2).build())
                .state(State.builder().stateId(3).build())
                .build();

        SolicitudeEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getSolicitudeId()).isEqualTo(domain.getSolicitudeId());
        assertThat(entity.getValue()).isEqualTo(domain.getValue());
        assertThat(entity.getDeadline()).isEqualTo(domain.getDeadline());
        assertThat(entity.getEmail()).isEqualTo(domain.getEmail());

        assertThat(entity.getLoanTypeId()).isEqualTo(domain.getLoanType().getLoanTypeId());
        assertThat(entity.getStateId()).isEqualTo(domain.getState().getStateId());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        Solicitude domain = mapper.toDomain(null);

        assertThat(domain).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        SolicitudeEntity entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }
}