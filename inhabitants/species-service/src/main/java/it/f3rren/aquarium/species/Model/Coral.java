package it.f3rren.aquarium.species.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

}   
