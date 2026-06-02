package it.f3rren.aquarium.parameters_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDTO {
    private Long id;
    private Long aquariumId;
    private Double temperature;
    private Double ph;
    private Integer salinity;
    private Integer orp;
    private LocalDateTime measuredAt;
}
