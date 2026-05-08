package it.f3rren.aquarium.maintenance_service.kafka.listener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.maintenance_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.maintenance_service.repository.IMaintenanceTaskRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AquariumEventListenerTest {

    @Mock
    private IMaintenanceTaskRepository maintenanceTaskRepository;

    @InjectMocks
    private AquariumEventListener listener;

    @Nested
    class OnAquariumEvent {

        @Test
        void deletesMaintenanceTasksWhenEventIsDeleted() {
            AquariumEvent event = new AquariumEvent(42L, "DELETED");

            listener.onAquariumEvent(event);

            verify(maintenanceTaskRepository).deleteAllByAquariumId(42L);
        }

        @Test
        void doesNothingWhenEventIsCreated() {
            AquariumEvent event = new AquariumEvent(42L, "CREATED");

            listener.onAquariumEvent(event);

            verify(maintenanceTaskRepository, never()).deleteAllByAquariumId(any());
        }

        @Test
        void doesNothingWhenEventTypeIsUnknown() {
            AquariumEvent event = new AquariumEvent(42L, "UPDATED");

            listener.onAquariumEvent(event);

            verify(maintenanceTaskRepository, never()).deleteAllByAquariumId(any());
        }
    }

    @Test
    void resourceDescriptionIsMaintenanceTasks() {
        assertEquals("maintenance tasks", listener.getResourceDescription());
    }
}
