package co.com.pragma.usecase.state;

import co.com.pragma.model.state.State;
import co.com.pragma.model.state.gateways.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StateUseCaseTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateUseCase stateUseCase;

    private State state1;
    private State state2;

    @BeforeEach
    void setUp() {
        state1 = State.builder().stateId(1).name("PENDING").description("Pending state").build();
        state2 = State.builder().stateId(2).name("APPROVED").description("Approved state").build();
    }

    @Nested
    class GetAllStatesTests {

        @Test
        void shouldReturnAllStatesFromRepository() {
            // Arrange
            when(stateRepository.findAll()).thenReturn(Flux.just(state1, state2));

            // Act
            Flux<State> result = stateUseCase.getAllStates();

            // Assert
            StepVerifier.create(result)
                    .expectNext(state1)
                    .expectNext(state2)
                    .verifyComplete();

            verify(stateRepository).findAll();
        }

        @Test
        void shouldReturnEmptyWhenRepositoryIsEmpty() {
            // Arrange
            when(stateRepository.findAll()).thenReturn(Flux.empty());

            // Act
            Flux<State> result = stateUseCase.getAllStates();

            // Assert
            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        void shouldPropagateErrorWhenRepositoryFails() {
            // Arrange
            RuntimeException exception = new RuntimeException("DB Error");
            when(stateRepository.findAll()).thenReturn(Flux.error(exception));

            // Act
            Flux<State> result = stateUseCase.getAllStates();

            // Assert
            StepVerifier.create(result)
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error".equals(throwable.getMessage()))
                    .verify();
        }
    }

    @Nested
    class SaveAllTests {

        @Test
        void shouldSaveOnlyNewStates() {
            // Arrange
            State newState = State.builder().name("NEW").build();
            State existingState = State.builder().name("EXISTING").build();
            List<State> statesToSave = List.of(newState, existingState);

            // Mocking repository behavior
            when(stateRepository.findByName("NEW")).thenReturn(Mono.empty()); // Does not exist
            when(stateRepository.findByName("EXISTING")).thenReturn(Mono.just(existingState)); // Exists
            when(stateRepository.saveAll(anyList())).thenAnswer(invocation -> Flux.fromIterable(invocation.getArgument(0)));

            // Act
            Flux<State> result = stateUseCase.saveAll(statesToSave);

            // Assert
            StepVerifier.create(result)
                    .expectNext(newState) // Only the new state should be returned
                    .verifyComplete();

            // Verify that saveAll was called only with the new state
            ArgumentCaptor<List<State>> captor = ArgumentCaptor.forClass(List.class);
            verify(stateRepository).saveAll(captor.capture());
            assertThat(captor.getValue()).hasSize(1);
            assertThat(captor.getValue().get(0).getName()).isEqualTo("NEW");
        }

        @Test
        void shouldNotSaveWhenAllStatesExist() {
            // Arrange
            List<State> existingStates = List.of(state1, state2);
            when(stateRepository.findByName(state1.getName())).thenReturn(Mono.just(state1));
            when(stateRepository.findByName(state2.getName())).thenReturn(Mono.just(state2));

            // Act
            Flux<State> result = stateUseCase.saveAll(existingStates);

            // Assert
            StepVerifier.create(result)
                    .verifyComplete();

            verify(stateRepository, never()).saveAll(anyList());
        }

        @Test
        void shouldNotDoAnythingForEmptyList() {
            // Arrange
            List<State> emptyList = Collections.emptyList();

            // Act
            Flux<State> result = stateUseCase.saveAll(emptyList);

            // Assert
            StepVerifier.create(result)
                    .verifyComplete();

            verify(stateRepository, never()).findByName(anyString());
            verify(stateRepository, never()).saveAll(anyList());
        }
    }
}