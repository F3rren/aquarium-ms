package it.f3rren.aquarium.manual_parameters_service.kafka.listener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.manual_parameters_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.manual_parameters_service.repository.IManualParameterRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AquariumEventListenerTest {

    @Mock
    private IManualParameterRepository manualParameterRepository;

    @InjectMocks
    private AquariumEventListener listener;

    @Nested
    class OnAquariumEvent {

        @Test
        void deletesManualParametersWhenEventIsDeleted() {
            AquariumEvent event = new AquariumEvent(42L, "DELETED");

            listener.onAquariumEvent(event);

            verify(manualParameterRepository).deleteAllByAquariumId(42L);
        }

        @Test
        void doesNothingWhenEventIsCreated() {
            AquariumEvent event = new AquariumEvent(42L, "CREATED");

            listener.onAquariumEvent(event);

            verify(manualParameterRepository, never()).deleteAllByAquariumId(any());
        }

        @Test
        void doesNothingWhenEventTypeIsUnknown() {
            AquariumEvent event = new AquariumEvent(42L, "UPDATED");

            listener.onAquariumEvent(event);

            verify(manualParameterRepository, never()).deleteAllByAquariumId(any());
        }
    }

    @Test
    void resourceDescriptionIsManualParameters() {
        assertEquals("manual parameters", listener.getResourceDescription());
    }
}
