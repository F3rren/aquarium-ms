package it.f3rren.aquarium.aquariums_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO class for updating aquarium details.
 * All fields are optional — only non-null fields are applied (partial update).
 * Lombok annotations are used for code generation.
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAquariumDTO {

    /**
     * Name of the aquarium. Optional for partial updates.
     * Must be between 2 and 100 characters if provided.
     */
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * Volume of the aquarium in liters. Optional for partial updates.
     * Must be a positive number no greater than 100,000 if provided.
     */
    @Positive(message = "Volume must be a positive number")
    @Max(value = 100000, message = "Volume cannot exceed 100,000 liters")
    private Integer volume;

    /**
     * Type of the aquarium. Optional for partial updates.
     * Must be either 'saltwater' or 'freshwater' if provided.
     */
    @Pattern(regexp = "^(saltwater|freshwater)$", message = "Type must be 'saltwater' or 'freshwater'")
    private String type;

    /**
     * Description of the aquarium. Optional for partial updates.
     * Must be at most 500 characters if provided.
     */
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    /**
     * URL of the aquarium image. Optional for partial updates.
     * Must be at most 2000 characters and a valid http/https URL if provided.
     */
    @Size(max = 2000, message = "Image URL must be at most 2000 characters")
    private String imageUrl;
}
