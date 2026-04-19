package it.f3rren.aquarium.species.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoralResponseDTO {
    private Long id;
    private String commonName;
    private String scientificName;
    private String type;
    private int minTankSize;
    private int maxSize;
    private String difficulty;
    private String lightRequirement;
    private String flowRequirement;
    private String placement;
    private boolean aggressive;
    private String feeding;
    private String description;
}
