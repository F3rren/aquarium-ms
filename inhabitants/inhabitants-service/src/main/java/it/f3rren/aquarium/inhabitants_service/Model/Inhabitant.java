package it.f3rren.aquarium.inhabitants_service.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "aquarium_inhabitants")
public class Inhabitant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "aquarium_id", nullable = false)
    private Long aquariumId;
    
    @Column(name = "inhabitant_type", nullable = false)
    private String inhabitantType; // "fish" o "coral"
    
    @Column(name = "inhabitant_id", nullable = false)
    private Long inhabitantId;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @CreationTimestamp
    @Column(name = "added_date")
    private LocalDateTime addedDate;
    
    @Column(name = "notes")
    private String notes;
    

}
