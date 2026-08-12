package it.f3rren.aquarium.aquariums_service.dto;

import it.f3rren.aquarium.aquariums_service.model.AquariumType;
import io.swagger.v3.oas.annotations.media.Schema;
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
     * Name of the aquarium. This field is required and must be between 2 and 100 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * Volume of the aquarium in liters. Must be a positive number no greater than 100,000.
     */
    @Positive(message = "Volume must be a positive number")
    @Max(value = 100000, message = "Volume cannot exceed 100,000 liters")
    private int volume;

    /**
     * Type of the aquarium. Required. Must be either 'saltwater' or 'freshwater'.
     */
    @NotNull(message = "Type is required and must be 'saltwater' or 'freshwater'")
    private AquariumType type;

    /**
     * Description of the aquarium. This field is optional and must be at most 500 characters.
     */
    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    /**
     * Image URL of the aquarium. Optional, must be a valid http/https URL if provided.
     */
    @Size(max = 2000, message = "Image URL must be at most 2000 characters")
    @Pattern(regexp = "^$|^https?://[^\\s/$.?#].[^\\s]*$", message = "Image URL must be a valid URL")
    private String imageUrl;

    /**
     * Verification/moderation marker. Not meant to be client-settable at all - see
     * {@link it.f3rren.aquarium.aquariums_service.model.Aquarium#verified}. Accepted here anyway,
     * on purpose, as this project's Mass Assignment / BOPLA test fixture. Hidden from the
     * generated OpenAPI schema ({@code @Schema(hidden = true)} only affects documentation, not
     * Jackson binding - the field is still read from the request body exactly as before) so a
     * schema-driven sample body doesn't already include it as a "documented" property: the whole
     * point of this fixture is a field the public API contract never advertises, but the server
     * binds anyway.
     */
    @Schema(hidden = true)
    @Size(max = 100, message = "Verified must be at most 100 characters")
    private String verified;
}
