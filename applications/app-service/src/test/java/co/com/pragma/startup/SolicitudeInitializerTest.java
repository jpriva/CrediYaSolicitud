package co.com.pragma.startup;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.state.State;
import co.com.pragma.usecase.state.StateUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudeInitializerTest {

    @Mock
    private StateUseCase stateUseCase;

    @Mock
    private ApplicationReadyEvent applicationReadyEvent;

    @InjectMocks
    private SolicitudeInitializer solicitudeInitializer;

    @Captor
    private ArgumentCaptor<List<State>> statesCaptor;

    @Test
    void onApplicationEvent_shouldCreateDefaultStates() {
        when(stateUseCase.saveAll(anyList())).thenReturn(Flux.empty());

        solicitudeInitializer.onApplicationEvent(applicationReadyEvent);

        verify(stateUseCase).saveAll(statesCaptor.capture());

        List<State> capturedStates = statesCaptor.getValue();
        assertThat(capturedStates).hasSize(4);

        List<String> stateNames = capturedStates.stream()
                .map(State::getName)
                .collect(Collectors.toList());

        assertThat(stateNames).containsExactlyInAnyOrder(
                DefaultValues.APPROVED_STATE,
                DefaultValues.PENDING_STATE,
                DefaultValues.MANUAL_STATE,
                DefaultValues.REJECTED_STATE
        );
    }
}