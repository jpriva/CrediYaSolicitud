package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.state.State;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import co.com.pragma.r2dbc.entity.StateEntity;
import org.reactivecommons.utils.ObjectMapper;

public final class SolicitudeMapper {

    private SolicitudeMapper() {
    }

    public static Solicitude toData(SolicitudeEntity solicitudeEntity, LoanTypeEntity loanTypeEntity, StateEntity stateEntity) {
        if (solicitudeEntity == null) {
            return null;
        }
        return Solicitude.builder()
                .solicitudeId(solicitudeEntity.getSolicitudeId())
                .value(solicitudeEntity.getValue())
                .term(solicitudeEntity.getTerm())
                .email(solicitudeEntity.getEmail())
                .state(stateEntity == null ? null :
                        State.builder()
                                .stateId(stateEntity.getStateId())
                                .name(stateEntity.getName())
                                .description(stateEntity.getDescription())
                                .build()
                )
                .loanType(loanTypeEntity == null ? null :
                        LoanType.builder()
                                .loanTypeId(loanTypeEntity.getLoanTypeId())
                                .name(loanTypeEntity.getName())
                                .minValue(loanTypeEntity.getMinValue())
                                .maxValue(loanTypeEntity.getMaxValue())
                                .interestRate(loanTypeEntity.getInterestRate())
                                .autoValidation(loanTypeEntity.getAutoValidation())
                                .build()
                )
                .build();
    }

    public static SolicitudeEntity toEntity(Solicitude solicitude) {
        if (solicitude == null) {
            return null;
        }
        return SolicitudeEntity.builder()
                .solicitudeId(solicitude.getSolicitudeId())
                .value(solicitude.getValue())
                .term(solicitude.getTerm())
                .email(solicitude.getEmail())
                .stateId(solicitude.getState() == null ? null : solicitude.getState().getStateId())
                .loanTypeId(solicitude.getLoanType() == null ? null : solicitude.getLoanType().getLoanTypeId())
                .build();
    }
}
