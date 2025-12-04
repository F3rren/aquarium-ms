package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterParameterDTO {
    private Long id;
    private Long aquariumId;
    private Double temperature;
    private Double ph;
    private Double salinity;
    private LocalDateTime measuredAt;
}
