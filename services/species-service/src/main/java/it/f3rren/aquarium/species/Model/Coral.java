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
@Table(name = "corals", schema = "inhabitants")
public class Coral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "light_requirement", nullable = false)
    private LightRequirement lightRequirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_requirement", nullable = false)
    private FlowRequirement flowRequirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "placement", nullable = false)
    private CoralPlacement placement;

    @Column(name = "aggressive", nullable = false)
    private boolean isAggressive;

    @Enumerated(EnumType.STRING)
    @Column(name = "feeding", nullable = false)
    private CoralFeeding feeding;

    @Column(name = "description", nullable = false)
    private String description;
}
