package it.f3rren.aquarium.maintenance_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.maintenance_service.dto.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;
import it.f3rren.aquarium.maintenance_service.repository.IMaintenanceTaskRepository;

@Service
public class MaintenanceTaskService {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceTaskService.class);

    private final IMaintenanceTaskRepository taskRepository;

    public MaintenanceTaskService(IMaintenanceTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public MaintenanceTask createTask(Long aquariumId, CreateMaintenanceTaskDTO dto) {
        MaintenanceTask task = new MaintenanceTask();
        task.setAquariumId(aquariumId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setFrequency(dto.getFrequency());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setNotes(dto.getNotes());
        task.setIsCompleted(false);

        log.info("Creating task '{}' for aquarium {}", dto.getTitle(), aquariumId);
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceTask> getAllTasks(Long aquariumId) {
        return taskRepository.findByAquariumIdOrderByDueDateAsc(aquariumId);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceTask> getPendingTasks(Long aquariumId) {
        return taskRepository.findByAquariumIdAndIsCompletedFalseOrderByDueDateAsc(aquariumId);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceTask> getTasksByStatus(Long aquariumId, Boolean isCompleted) {
        return taskRepository.findByAquariumIdAndIsCompletedOrderByDueDateAsc(aquariumId, isCompleted);
    }

    @Transactional
    public MaintenanceTask updateTask(Long aquariumId, Long taskId, UpdateMaintenanceTaskDTO dto) {
        MaintenanceTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (!task.getAquariumId().equals(aquariumId)) {
            throw new ResourceNotFoundException("Task " + taskId + " does not belong to aquarium " + aquariumId);
        }

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getFrequency() != null) task.setFrequency(dto.getFrequency());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getNotes() != null) task.setNotes(dto.getNotes());

        log.info("Updating task {} in aquarium {}", taskId, aquariumId);
        return taskRepository.save(task);
    }

    @Transactional
    public MaintenanceTask completeTask(Long aquariumId, Long taskId) {
        MaintenanceTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (!task.getAquariumId().equals(aquariumId)) {
            throw new ResourceNotFoundException("Task " + taskId + " does not belong to aquarium " + aquariumId);
        }

        task.setIsCompleted(true);
        task.setCompletedAt(LocalDateTime.now());
        log.info("Completing task {} in aquarium {}", taskId, aquariumId);
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long aquariumId, Long taskId) {
        MaintenanceTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (!task.getAquariumId().equals(aquariumId)) {
            throw new ResourceNotFoundException("Task " + taskId + " does not belong to aquarium " + aquariumId);
        }

        log.info("Deleting task {} from aquarium {}", taskId, aquariumId);
        taskRepository.deleteById(taskId);
    }
}
