package co.com.pragma.startup;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.state.State;
import co.com.pragma.usecase.state.StateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SolicitudeInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final StateUseCase stateUseCase;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createStates();
    }

    private void createStates() {
        State approved = State.builder().name(DefaultValues.APPROVED_STATE).description(DefaultValues.APPROVED_STATE_DESC).build();
        State pending = State.builder().name(DefaultValues.PENDING_STATE).description(DefaultValues.PENDING_STATE_DESC).build();
        State manual = State.builder().name(DefaultValues.MANUAL_STATE).description(DefaultValues.MANUAL_STATE_DESC).build();
        State rejected = State.builder().name(DefaultValues.REJECTED_STATE).description(DefaultValues.REJECTED_STATE_DESC).build();
        List<State> states = List.of(approved, pending, manual, rejected);
        stateUseCase.saveAll(states).subscribe();
    }
}
