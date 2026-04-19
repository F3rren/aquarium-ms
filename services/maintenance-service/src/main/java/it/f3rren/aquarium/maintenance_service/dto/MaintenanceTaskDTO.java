package it.f3rren.aquarium.maintenance_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTaskDTO {
    private Long id;
    private Long aquariumId;
    private String title;
    private String description;
    private String frequency;
    private String priority;
    private Boolean isCompleted;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private String notes;
}
