package it.f3rren.aquarium.inhabitants_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for partial updates to an inhabitant record.
 * All fields are optional — only non-null fields are applied.
 *
 * <p><strong>Null limitation:</strong> a {@code null} field means "no change" and the existing
 * value is preserved. Explicitly clearing an optional field (e.g. {@code notes}) requires a
 * dedicated PATCH endpoint.</p>
 *
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInhabitantDTO {

    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    @Size(max = 100, message = "Custom name must be at most 100 characters")
    private String customName;

    @Positive(message = "Current size must be a positive number")
    private Integer currentSize;

    @Size(max = 50, message = "Custom difficulty must be at most 50 characters")
    private String customDifficulty;

    @Size(max = 50, message = "Custom temperament must be at most 50 characters")
    private String customTemperament;

    @Size(max = 500, message = "Custom diet must be at most 500 characters")
    private String customDiet;

    private Boolean isReefSafe;

    @Positive(message = "Custom min tank size must be a positive number")
    private Integer customMinTankSize;
}
