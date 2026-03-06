package it.f3rren.aquarium.aquariums_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for creating a new aquarium.
 * This class is used to validate the input data when creating a new aquarium.
 * Lombok annotations are used to generate getters, setters, equals, hashCode, and toString methods.
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAquariumDTO {

    /**
     * Name of the aquarium. This field is required and must be between 1 and 100 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    /**
     * Volume of the aquarium. This field is required and must be a positive number.
     * The units for volume are liters. This field is not required, but if provided, it must be positive.
     * The default value is 100 liters. If the volume is provided, it must be greater than 0.
     */
    @Positive(message = "Volume must be a positive number")
    private int volume;

    /**
     * Type of the aquarium. This field is optional and must be at most 50 characters.
     */
    @Size(max = 50, message = "Type must be at most 50 characters")
    private String type;

    /**
     * Description of the aquarium. This field is optional and must be at most 500 characters.
     */
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    /**
     * Image URL of the aquarium. This field is optional and must be at most 500 characters.
     * The URL must be a valid URL format. If the URL is provided, it must be a valid URL.
     * The default value is an empty string.
     */
    @Size(max = 500, message = "Image URL must be at most 500 characters")
    @URL(message = "Image URL must be a valid URL")
    private String imageUrl;
}
