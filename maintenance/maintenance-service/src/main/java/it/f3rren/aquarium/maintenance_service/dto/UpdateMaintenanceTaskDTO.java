package it.f3rren.aquarium.maintenance_service.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMaintenanceTaskDTO {

    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    @Size(max = 50, message = "Frequency must be at most 50 characters")
    private String frequency;

    @Size(max = 50, message = "Priority must be at most 50 characters")
    private String priority;

    private LocalDateTime dueDate;

    @Size(max = 2000, message = "Notes must be at most 2000 characters")
    private String notes;
}
