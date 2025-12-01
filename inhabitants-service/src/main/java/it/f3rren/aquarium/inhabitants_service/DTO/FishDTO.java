package it.f3rren.aquarium.inhabitants_service.DTO;

import lombok.Data;

@Data
public class FishDTO {
    private int id;
    private String commonName;
    private String scientificName;
    private String family;
    private int minTankSize;
    private int maxSize;
    private String difficulty;
    private boolean reefSafe;
    private String temperament;
    private String diet;
    private String imageUrl;
    private String description;
    private String waterType;
}
