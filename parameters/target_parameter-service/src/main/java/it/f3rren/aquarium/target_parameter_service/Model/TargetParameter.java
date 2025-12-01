package it.f3rren.aquarium.target_parameter_service.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "target_parameters")
public class TargetParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "aquarium_id", nullable = false, unique = true)
    private Long aquariumId;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "ph")
    private Double ph;
    
    @Column(name = "salinity")
    private Double salinity;
    
    @Column(name = "orp")
    private Double orp;
    
    @Column(name = "calcium")
    private Double calcium;
    
    @Column(name = "magnesium")
    private Double magnesium;
    
    @Column(name = "kh")
    private Double kh;
    
    @Column(name = "nitrate")
    private Double nitrate;
    
    @Column(name = "phosphate")
    private Double phosphate;
}
