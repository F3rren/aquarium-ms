package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;

import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import lombok.*;

/**
 * Read-only DTO returned by all aquarium endpoints.
 *
 * <p>Decouples the API contract from the {@link Aquarium} entity so that
 * internal model changes do not affect the public interface.
 * Use the {@link #fromEntity(Aquarium)} factory method to construct instances.</p>
 *
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AquariumResponseDTO {

    /** Database-assigned identifier of the aquarium. */
    private Long id;

    /** Display name of the aquarium. */
    private String name;

    /** Water volume in liters. */
    private int volume;

    /** Aquarium type: {@code "saltwater"} or {@code "freshwater"}. */
    private String type;

    /** Timestamp of when the aquarium was first created in the system. */
    private LocalDateTime createdAt;

    /** Optional free-text description provided by the user. */
    private String description;

    /** Optional URL pointing to an image of the aquarium. */
    private String imageUrl;

    /**
     * Maps an {@link Aquarium} entity to its response representation.
     *
     * @param aquarium the entity to convert; must not be {@code null}
     * @return a populated {@link AquariumResponseDTO}
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
