package it.f3rren.aquarium.maintenance_service.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import it.f3rren.aquarium.maintenance_service.model.TaskFrequency;
import it.f3rren.aquarium.maintenance_service.model.TaskPriority;

/**
 * DTO for partial updates to a maintenance task.
 * All fields are optional — only non-null fields are applied.
 *
 * <p><strong>Null limitation:</strong> a {@code null} field means "no change" and the existing
 * value is preserved. Explicitly clearing an optional field requires a dedicated PATCH endpoint.</p>
 *
 * @author F3rren
 */
@Getter
@Setter
public class UpdateMaintenanceTaskDTO {

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
