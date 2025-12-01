package it.f3rren.aquarium.inhabitants_service.DTO;

import lombok.Data;

@Data
public class CoralDTO {
    private int id;
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
