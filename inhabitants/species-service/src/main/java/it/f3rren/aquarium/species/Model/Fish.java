package it.f3rren.aquarium.species.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

}
