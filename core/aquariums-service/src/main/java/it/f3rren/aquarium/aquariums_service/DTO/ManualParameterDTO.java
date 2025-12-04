package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualParameterDTO {
    private Long id;
    private Long aquariumId;
    private Double calcium;
    private Double magnesium;
    private Double kh;
    private Double nitrate;
    private Double phosphate;
    private LocalDateTime measuredAt;
    private String notes;
}
