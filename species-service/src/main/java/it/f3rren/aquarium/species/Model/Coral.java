package it.f3rren.aquarium.species.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "corals", schema = "inhabitants")
public class Coral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "common_name", nullable = false)
    private String commonName;

    @Column(name = "scientific_name", nullable = false)
    private String scientificName;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "min_tank_size", nullable = false)
    private int minTankSize;

    @Column(name = "max_size", nullable = false)
    private int maxSize;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "light_requirement", nullable = false)
    private String lightRequirement;

    @Column(name = "flow_requirement", nullable = false)
    private String flowRequirement;

    @Column(name = "placement", nullable = false)
    private String placement;

    @Column(name = "aggressive", nullable = false)
    private boolean isAggressive;

    @Column(name = "feeding", nullable = false)
    private String feeding;

    @Column(name = "description", nullable = false)
    private String description;

    public Coral() {}

    public Coral(int id, String commonName, String scientificName, String type, int minTankSize, int maxSize,
            String difficulty, String lightRequirement, String flowRequirement, String placement, boolean isAggressive,
            String feeding, String description) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.type = type;
        this.minTankSize = minTankSize;
        this.maxSize = maxSize;
        this.difficulty = difficulty;
        this.lightRequirement = lightRequirement;
        this.flowRequirement = flowRequirement;
        this.placement = placement;
        this.isAggressive = isAggressive;
        this.feeding = feeding;
        this.description = description;
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
    public String getLightRequirement() {
        return lightRequirement;
    }
    public void setLightRequirement(String lightRequirement) {
        this.lightRequirement = lightRequirement;
    }
    public String getFlowRequirement() {
        return flowRequirement;
    }
    public void setFlowRequirement(String flowRequirement) {
        this.flowRequirement = flowRequirement;
    }
    public String getPlacement() {
        return placement;
    }
    public void setPlacement(String placement) {
        this.placement = placement;
    }
    public boolean isAggressive() {
        return isAggressive;
    }
    public void setAggressive(boolean isAggressive) {
        this.isAggressive = isAggressive;
    }
    public String getFeeding() {
        return feeding;
    }
    public void setFeeding(String feeding) {
        this.feeding = feeding;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}   
