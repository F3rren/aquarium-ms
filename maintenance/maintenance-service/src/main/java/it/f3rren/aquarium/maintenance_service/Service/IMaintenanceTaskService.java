package it.f3rren.aquarium.maintenance_service.service;

import java.util.List;

import it.f3rren.aquarium.maintenance_service.dto.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;

public interface IMaintenanceTaskService {

    MaintenanceTask createTask(Long aquariumId, CreateMaintenanceTaskDTO dto);

    List<MaintenanceTask> getAllTasks(Long aquariumId);

    List<MaintenanceTask> getPendingTasks(Long aquariumId);

    List<MaintenanceTask> getTasksByStatus(Long aquariumId, Boolean isCompleted);

    MaintenanceTask updateTask(Long aquariumId, Long taskId, UpdateMaintenanceTaskDTO dto);

    MaintenanceTask completeTask(Long aquariumId, Long taskId);

    void deleteTask(Long aquariumId, Long taskId);
}
