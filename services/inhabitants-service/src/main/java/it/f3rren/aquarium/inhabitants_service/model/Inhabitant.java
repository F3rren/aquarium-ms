package it.f3rren.aquarium.inhabitants_service.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "aquarium_inhabitants")
public class Inhabitant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aquarium_id", nullable = false, updatable = false)
    private Long aquariumId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "inhabitant_type", nullable = false)
    private InhabitantType inhabitantType;
    
    @Column(name = "inhabitant_id", nullable = false)
    private Long inhabitantId;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @CreationTimestamp
    @Column(name = "added_date", updatable = false)
    private LocalDateTime addedDate;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    // Custom fields - override species defaults based on actual observations
    @Column(name = "custom_name")
    private String customName; // Custom name for this specific inhabitant
    
    @Column(name = "current_size")
    private Integer currentSize; // Current size in cm
    
    @Column(name = "custom_difficulty")
    private String customDifficulty; // Observed difficulty level (easy, moderate, difficult)
    
    @Column(name = "custom_temperament")
    private String customTemperament; // Actual temperament observed
    
    @Column(name = "custom_diet", length = 500)
    private String customDiet; // Specific diet preferences observed
    
    @Column(name = "is_reef_safe")
    private Boolean isReefSafe; // Override reef safe status based on observation
    
    @Column(name = "custom_min_tank_size")
    private Integer customMinTankSize; // Minimum tank size based on observation
    

}

