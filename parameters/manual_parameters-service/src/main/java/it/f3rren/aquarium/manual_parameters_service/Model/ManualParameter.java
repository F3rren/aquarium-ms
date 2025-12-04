package it.f3rren.aquarium.manual_parameters_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "manual_parameters")
public class ManualParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "aquarium_id", nullable = false)
    private Long aquariumId;
    
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
    
    @CreationTimestamp
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;
    
    @Column(name = "notes")
    private String notes;
}
