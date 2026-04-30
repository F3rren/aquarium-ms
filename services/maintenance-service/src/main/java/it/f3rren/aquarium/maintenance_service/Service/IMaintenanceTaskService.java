package it.f3rren.aquarium.maintenance_service.service;

import java.util.List;

import it.f3rren.aquarium.maintenance_service.dto.request.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.response.MaintenanceTaskDTO;

public interface IMaintenanceTaskService {

    MaintenanceTaskDTO createTask(Long aquariumId, CreateMaintenanceTaskDTO dto);

    List<MaintenanceTaskDTO> getAllTasks(Long aquariumId);

    List<MaintenanceTaskDTO> getPendingTasks(Long aquariumId);

    List<MaintenanceTaskDTO> getTasksByStatus(Long aquariumId, Boolean isCompleted);

    MaintenanceTaskDTO updateTask(Long aquariumId, Long taskId, UpdateMaintenanceTaskDTO dto);

    MaintenanceTaskDTO completeTask(Long aquariumId, Long taskId);

    void deleteTask(Long aquariumId, Long taskId);
}
