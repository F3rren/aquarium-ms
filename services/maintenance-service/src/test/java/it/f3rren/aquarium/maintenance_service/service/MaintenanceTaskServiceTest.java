package it.f3rren.aquarium.maintenance_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.maintenance_service.dto.CreateMaintenanceTaskDTO;
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

    @Test
    void createTask_savesAndReturnsTask() {
        CreateMaintenanceTaskDTO dto = new CreateMaintenanceTaskDTO();
        dto.setTitle("Water change");
        dto.setPriority("high");

        MaintenanceTask saved = new MaintenanceTask();
        saved.setId(1L);
        saved.setAquariumId(1L);
        saved.setTitle("Water change");

        when(taskRepository.save(any(MaintenanceTask.class))).thenReturn(saved);

        MaintenanceTask result = taskService.createTask(1L, dto);

        assertEquals("Water change", result.getTitle());
        verify(taskRepository).save(any(MaintenanceTask.class));
    }

    @Test
    void getAllTasks_returnsListForAquarium() {
        MaintenanceTask t1 = new MaintenanceTask();
        t1.setId(1L);
        t1.setAquariumId(1L);
        t1.setTitle("Water change");

        when(taskRepository.findByAquariumIdOrderByDueDateAsc(1L)).thenReturn(List.of(t1));

        List<MaintenanceTask> result = taskService.getAllTasks(1L);

        assertEquals(1, result.size());
        verify(taskRepository).findByAquariumIdOrderByDueDateAsc(1L);
    }

    @Test
    void completeTask_setsCompletedFlagAndTimestamp() {
        MaintenanceTask task = new MaintenanceTask();
        task.setId(1L);
        task.setAquariumId(1L);
        task.setIsCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(MaintenanceTask.class))).thenReturn(task);

        MaintenanceTask result = taskService.completeTask(1L, 1L);

        assertTrue(result.getIsCompleted());
        assertNotNull(result.getCompletedAt());
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_throwsWhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(1L, 99L));
    }

    @Test
    void updateTask_throwsWhenAquariumMismatch() {
        MaintenanceTask task = new MaintenanceTask();
        task.setId(1L);
        task.setAquariumId(2L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(1L, 1L, new UpdateMaintenanceTaskDTO()));
    }
}
