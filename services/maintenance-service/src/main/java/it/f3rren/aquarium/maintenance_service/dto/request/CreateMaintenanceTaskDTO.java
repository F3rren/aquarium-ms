package it.f3rren.aquarium.maintenance_service.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import it.f3rren.aquarium.maintenance_service.model.TaskFrequency;
import it.f3rren.aquarium.maintenance_service.model.TaskPriority;

@Getter
@Setter
public class CreateMaintenanceTaskDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    private TaskFrequency frequency;

    private TaskPriority priority;

    private LocalDateTime dueDate;

    @Size(max = 2000, message = "Notes must be at most 2000 characters")
    private String notes;
}
