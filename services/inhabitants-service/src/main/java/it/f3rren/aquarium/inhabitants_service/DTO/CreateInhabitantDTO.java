package it.f3rren.aquarium.inhabitants_service.dto;

import it.f3rren.aquarium.inhabitants_service.model.InhabitantType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInhabitantDTO {

    @NotNull(message = "Inhabitant type is required")
    private InhabitantType inhabitantType;

    @NotNull(message = "Inhabitant ID (species reference) is required")
    @Positive(message = "Inhabitant ID must be a positive number")
    private Long inhabitantId;

    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    @Size(max = 100, message = "Custom name must be at most 100 characters")
    private String customName;
}
