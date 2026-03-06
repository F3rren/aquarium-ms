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
     * Name field, represents the name of the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    /**
     * Volume field, represents the volume of the aquarium.
     * Must be a positive number.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    @Positive(message = "Volume must be a positive number")
    private Integer volume;

    /**
     * Type field, represents the type of the aquarium.
     * Must be at most 50 characters.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    @Size(max = 50, message = "Type must be at most 50 characters")
    private String type;

    /**
     * Description field, represents the description of the aquarium.
     * Must be at most 500 characters.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    /**
     * Image URL field, represents the URL of the aquarium image.
     * Must be at most 500 characters.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    @Size(max = 500, message = "Image URL must be at most 500 characters")
    private String imageUrl;
}
