package it.f3rren.aquarium.parameters_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "water_parameters")
public class Parameter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "aquarium_id", nullable = false)
    private Long aquariumId;
    
    @Column(name = "temperature", nullable = false)
    private Double temperature;
    
    @Column(name = "ph", nullable = false)
    private Double ph;
    
    @Column(name = "salinity", nullable = false)
    private Integer salinity;
    
    @Column(name = "orp", nullable = false)
    private Integer orp;
    
    @CreationTimestamp
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;
}
