package it.f3rren.aquarium.aquariums_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAquariumDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Positive(message = "Volume must be a positive number")
    private int volume;

    @Size(max = 50, message = "Type must be at most 50 characters")
    private String type;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @Size(max = 500, message = "Image URL must be at most 500 characters")
    private String imageUrl;
}
