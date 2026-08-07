package it.f3rren.aquarium.species.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "fish", schema = "inhabitants")
public class Fish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name = "reef_safe", nullable = false)
    private boolean isReefSafe;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperament", nullable = false)
    private FishTemperament temperament;

    @Enumerated(EnumType.STRING)
    @Column(name = "diet", nullable = false)
    private FishDiet diet;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "water_type")
    private WaterType waterType;
}
