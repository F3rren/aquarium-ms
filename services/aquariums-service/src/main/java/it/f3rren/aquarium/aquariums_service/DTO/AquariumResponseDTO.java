package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;

import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import lombok.*;

/**
 * DTO representing the response for an aquarium.
 * It is used to encapsulate the data of an aquarium for API responses.
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AquariumResponseDTO {

    /**
     * The ID of the aquarium. Not null because of @NotNull annotations.
     */
    private Long id;

    /**
     * The name of the aquarium. Not null because of @NotNull annotations.
     */
    private String name;

    /**
     * The volume of the aquarium. Not null because of @NotNull annotations.
     */
    private int volume;

    /**
     * The type of the aquarium. Not null because of @NotNull annotations.
     */
    private String type;

    /**
     * The creation date of the aquarium. Not null because of @NotNull annotations.
     */
    private LocalDateTime createdAt;

    /**
     * The description of the aquarium. Not null because of @NotNull annotations.
     */
    private String description;

    /**
     * The URL of the aquarium's image. Not null because of @NotNull annotations.
     */
    private String imageUrl;

    /**
     * Constructs an AquariumResponseDTO from an Aquarium entity.
     * @param aquarium The Aquarium entity to convert.
     * @return AquariumResponseDTO with data from the Aquarium entity.
     */
    public static AquariumResponseDTO fromEntity(Aquarium aquarium) {
        return AquariumResponseDTO.builder()
                .id(aquarium.getId())
                .name(aquarium.getName())
                .volume(aquarium.getVolume())
                .type(aquarium.getType())
                .createdAt(aquarium.getCreatedAt())
                .description(aquarium.getDescription())
                .imageUrl(aquarium.getImageUrl())
                .build();
    }
}
