package it.f3rren.aquarium.target_parameter_service.kafka.listener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.target_parameter_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.target_parameter_service.repository.ITargetParameterRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AquariumEventListenerTest {

    @Mock
    private ITargetParameterRepository targetParameterRepository;

    @InjectMocks
    private AquariumEventListener listener;

    @Nested
    class OnAquariumEvent {

        @Test
        void deletesTargetParametersWhenEventIsDeleted() {
            AquariumEvent event = new AquariumEvent(42L, "DELETED");

            listener.onAquariumEvent(event);

            verify(targetParameterRepository).deleteAllByAquariumId(42L);
        }

        @Test
        void doesNothingWhenEventIsCreated() {
            AquariumEvent event = new AquariumEvent(42L, "CREATED");

            listener.onAquariumEvent(event);

            verify(targetParameterRepository, never()).deleteAllByAquariumId(any());
        }

        @Test
        void doesNothingWhenEventTypeIsUnknown() {
            AquariumEvent event = new AquariumEvent(42L, "UPDATED");

            listener.onAquariumEvent(event);

            verify(targetParameterRepository, never()).deleteAllByAquariumId(any());
        }
    }

    @Test
    void resourceDescriptionIsTargetParameters() {
        assertEquals("target parameters", listener.getResourceDescription());
    }
}
