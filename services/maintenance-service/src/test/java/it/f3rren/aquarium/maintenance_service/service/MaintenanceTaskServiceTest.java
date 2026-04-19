package it.f3rren.aquarium.maintenance_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.maintenance_service.dto.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.MaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;
import it.f3rren.aquarium.maintenance_service.repository.IMaintenanceTaskRepository;

@ExtendWith(MockitoExtension.class)
class MaintenanceTaskServiceTest {

    @Mock
    private IMaintenanceTaskRepository taskRepository;

    @InjectMocks
    private MaintenanceTaskService taskService;

    private MaintenanceTask sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new MaintenanceTask();
        sampleTask.setId(1L);
        sampleTask.setAquariumId(1L);
        sampleTask.setTitle("Water change");
        sampleTask.setIsCompleted(false);
    }

    @Nested
    class CreateTask {

        @Test
        void savesAndReturnsDto() {
            CreateMaintenanceTaskDTO dto = new CreateMaintenanceTaskDTO();
            dto.setTitle("Water change");
            dto.setPriority("high");

            when(taskRepository.save(any(MaintenanceTask.class))).thenReturn(sampleTask);

            MaintenanceTaskDTO result = taskService.createTask(1L, dto);

            assertEquals("Water change", result.getTitle());
            verify(taskRepository).save(any(MaintenanceTask.class));
        }
    }

    @Nested
    class GetAllTasks {

        @Test
        void returnsDtoListForAquarium() {
            when(taskRepository.findByAquariumIdOrderByDueDateAsc(1L)).thenReturn(List.of(sampleTask));

            List<MaintenanceTaskDTO> result = taskService.getAllTasks(1L);

            assertEquals(1, result.size());
            assertEquals("Water change", result.get(0).getTitle());
            verify(taskRepository).findByAquariumIdOrderByDueDateAsc(1L);
        }
    }

    @Nested
    class UpdateTask {

        @Test
        void updatesAndReturnsDto() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(any(MaintenanceTask.class))).thenReturn(sampleTask);

            UpdateMaintenanceTaskDTO dto = new UpdateMaintenanceTaskDTO();
            dto.setTitle("Oil change");

            MaintenanceTaskDTO result = taskService.updateTask(1L, 1L, dto);

            assertEquals("Oil change", result.getTitle());
            verify(taskRepository).save(sampleTask);
        }

        @Test
        void throwsWhenNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> taskService.updateTask(1L, 99L, new UpdateMaintenanceTaskDTO()));
        }

        @Test
        void throwsWhenAquariumMismatch() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

            assertThrows(IllegalArgumentException.class,
                    () -> taskService.updateTask(99L, 1L, new UpdateMaintenanceTaskDTO()));
        }
    }

    @Nested
    class CompleteTask {

        @Test
        void setsCompletedFlagAndTimestamp() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(any(MaintenanceTask.class))).thenReturn(sampleTask);

            MaintenanceTaskDTO result = taskService.completeTask(1L, 1L);

            assertTrue(result.getIsCompleted());
            assertNotNull(result.getCompletedAt());
            verify(taskRepository).save(sampleTask);
        }

        @Test
        void throwsWhenAquariumMismatch() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

            assertThrows(IllegalArgumentException.class,
                    () -> taskService.completeTask(99L, 1L));
        }
    }

    @Nested
    class DeleteTask {

        @Test
        void deletesExistingTask() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

            taskService.deleteTask(1L, 1L);

            verify(taskRepository).deleteById(1L);
        }

        @Test
        void throwsWhenNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L, 99L));
        }

        @Test
        void throwsWhenAquariumMismatch() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

            assertThrows(IllegalArgumentException.class,
                    () -> taskService.deleteTask(99L, 1L));
            verify(taskRepository, never()).deleteById(any());
        }
    }
}
