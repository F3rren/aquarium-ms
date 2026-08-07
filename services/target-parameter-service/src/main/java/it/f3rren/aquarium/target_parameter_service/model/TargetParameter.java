package it.f3rren.aquarium.target_parameter_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "target_parameters")
public class TargetParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "aquarium_id", nullable = false, unique = true, updatable = false)
    private Long aquariumId;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "ph")
    private Double ph;
    
    @Column(name = "salinity")
    private Double salinity;
    
    @Column(name = "orp")
    private Double orp;
}
