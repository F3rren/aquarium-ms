package it.f3rren.aquarium.species.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "fishs", schema = "inhabitants")
public class Fish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "common_name", nullable = false)
    private String commonName;
    
    @Column(name = "scientific_name", nullable = false)
    private String scientificName;

    @Column(name = "family", nullable = false)
    private String family;

    @Column(name = "min_tank_size", nullable = false)
    private int minTankSize;

    @Column(name = "max_size", nullable = false)
    private int maxSize;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "reef_safe", nullable = false)
    private boolean isReefSafe;

    @Column(name = "temperament", nullable = false)
    private String temperament;

    @Column(name = "diet", nullable = false)
    private String diet;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "water_type", nullable = true)
    private String waterType;

    public Fish() {}

    public Fish(int id, String commonName, String scientificName, String family, int minTankSize, int maxSize,
            String difficulty, boolean isReefSafe, String temperament, String diet, String imageUrl, String description,
            String waterType) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.family = family;
        this.minTankSize = minTankSize;
        this.maxSize = maxSize;
        this.difficulty = difficulty;
        this.isReefSafe = isReefSafe;
        this.temperament = temperament;
        this.diet = diet;
        this.imageUrl = imageUrl;
        this.description = description;
        this.waterType = waterType;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCommonName() {
        return commonName;
    }
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    public String getScientificName() {
        return scientificName;
    }
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
    public String getFamily() {
        return family;
    }
    public void setFamily(String family) {
        this.family = family;
    }
    public int getMinTankSize() {
        return minTankSize;
    }
    public void setMinTankSize(int minTankSize) {
        this.minTankSize = minTankSize;
    }
    public int getMaxSize() {
        return maxSize;
    }
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public boolean isReefSafe() {
        return isReefSafe;
    }
    public void setReefSafe(boolean isReefSafe) {
        this.isReefSafe = isReefSafe;
    }
    public String getTemperament() {
        return temperament;
    }
    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }
    public String getDiet() {
        return diet;
    }
    public void setDiet(String diet) {
        this.diet = diet;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

}
