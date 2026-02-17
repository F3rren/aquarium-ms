package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;

import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AquariumResponseDTO {

    private Long id;
    private String name;
    private int volume;
    private String type;
    private LocalDateTime createdAt;
    private String description;
    private String imageUrl;

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
