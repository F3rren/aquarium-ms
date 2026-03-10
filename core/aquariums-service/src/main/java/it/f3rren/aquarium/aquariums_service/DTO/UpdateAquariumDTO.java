package it.f3rren.aquarium.aquariums_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO class for updating aquarium details.
 * This class is used to transfer aquarium update information between the application layers.
 * Lombok annotations are used for code generation.
 * Fields are annotated with validation constraints to ensure the data integrity.
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAquariumDTO {

    /**
     * Name of the aquarium. Optional for partial updates.
     * Must be between 1 and 100 characters if provided.
     */
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    /**
     * Volume of the aquarium in liters. Optional for partial updates.
     * Must be a positive number if provided.
     */
    @Positive(message = "Volume must be a positive number")
    private Integer volume;

    /**
     * Type of the aquarium (e.g. "saltwater", "freshwater"). Optional for partial updates.
     * Must be at most 50 characters if provided.
     */
    @Size(max = 50, message = "Type must be at most 50 characters")
    private String type;

    /**
     * Description of the aquarium. Optional for partial updates.
     * Must be at most 500 characters if provided.
     */
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    /**
     * URL of the aquarium image. Optional for partial updates.
     * Must be at most 500 characters if provided.
     */
    @Size(max = 500, message = "Image URL must be at most 500 characters")
    private String imageUrl;
}
