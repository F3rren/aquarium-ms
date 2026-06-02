package it.f3rren.aquarium.maintenance_service.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import it.f3rren.aquarium.maintenance_service.model.TaskFrequency;
import it.f3rren.aquarium.maintenance_service.model.TaskPriority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTaskDTO {
    private Long id;
    private Long aquariumId;
    private String title;
    private String description;
    private TaskFrequency frequency;
    private TaskPriority priority;
    private Boolean isCompleted;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private String notes;
}
