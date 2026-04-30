package it.f3rren.aquarium.maintenance_service.mapper;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.maintenance_service.dto.response.MaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;

@Component
public class MaintenanceTaskMapper {

    public MaintenanceTaskDTO toDTO(MaintenanceTask task) {
        MaintenanceTaskDTO dto = new MaintenanceTaskDTO();
        dto.setId(task.getId());
        dto.setAquariumId(task.getAquariumId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setFrequency(task.getFrequency());
        dto.setPriority(task.getPriority());
        dto.setIsCompleted(task.getIsCompleted());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setNotes(task.getNotes());
        return dto;
    }
}
