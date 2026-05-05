package it.f3rren.aquarium.water_parameters_service.kafka.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.parameters_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.parameters_service.kafka.listener.AquariumEventListener;
import it.f3rren.aquarium.parameters_service.repository.IParameterRepository;

@ExtendWith(MockitoExtension.class)
class AquariumEventListenerTest {

    @Mock
    private IParameterRepository parameterRepository;

    @InjectMocks
    private AquariumEventListener listener;

    @Nested
    class OnAquariumEvent {

        @Test
        void deletesWaterParametersWhenEventIsDeleted() {
            AquariumEvent event = new AquariumEvent(42L, "DELETED");

            listener.onAquariumEvent(event);

            verify(parameterRepository).deleteAllByAquariumId(42L);
        }

        @Test
        void doesNothingWhenEventIsCreated() {
            AquariumEvent event = new AquariumEvent(42L, "CREATED");

            listener.onAquariumEvent(event);

            verify(parameterRepository, never()).deleteAllByAquariumId(any());
        }

        @Test
        void doesNothingWhenEventTypeIsUnknown() {
            AquariumEvent event = new AquariumEvent(42L, "UPDATED");

            listener.onAquariumEvent(event);

            verify(parameterRepository, never()).deleteAllByAquariumId(any());
        }
    }

    @Test
    void resourceDescriptionIsWaterParameters() {
        assertEquals("water parameters", listener.getResourceDescription());
    }
}
