package it.f3rren.aquarium.maintenance_service.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.maintenance_service.Exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.Model.MaintenanceTask;
import it.f3rren.aquarium.maintenance_service.Repository.IMaintenanceTaskRepository;


@Service
public class MaintenanceTaskService {
    
    @Autowired
    private IMaintenanceTaskRepository taskRepository;
    
    public MaintenanceTask createTask(Long aquariumId, MaintenanceTask task) {
        task.setAquariumId(aquariumId);
        if (task.getIsCompleted() == null) {
            task.setIsCompleted(false);
        }
        return taskRepository.save(task);
    }
    
    public List<MaintenanceTask> getAllTasks(Long aquariumId) {
        return taskRepository.findByAquariumIdOrderByDueDateAsc(aquariumId);
    }
    
    public List<MaintenanceTask> getPendingTasks(Long aquariumId) {
        return taskRepository.findByAquariumIdAndIsCompletedFalseOrderByDueDateAsc(aquariumId);
    }
    
    public List<MaintenanceTask> getTasksByStatus(Long aquariumId, Boolean isCompleted) {
        return taskRepository.findByAquariumIdAndIsCompletedOrderByDueDateAsc(aquariumId, isCompleted);
    }
    
    public Optional<MaintenanceTask> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }
    
    public MaintenanceTask updateTask(Long taskId, MaintenanceTask updatedTask) {
        MaintenanceTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (updatedTask.getTitle() != null) task.setTitle(updatedTask.getTitle());
        if (updatedTask.getDescription() != null) task.setDescription(updatedTask.getDescription());
        if (updatedTask.getFrequency() != null) task.setFrequency(updatedTask.getFrequency());
        if (updatedTask.getPriority() != null) task.setPriority(updatedTask.getPriority());
        if (updatedTask.getDueDate() != null) task.setDueDate(updatedTask.getDueDate());
        if (updatedTask.getNotes() != null) task.setNotes(updatedTask.getNotes());
        
        return taskRepository.save(task);
    }
    
    public MaintenanceTask completeTask(Long taskId) {
        MaintenanceTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setIsCompleted(true);
        task.setCompletedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }
}
