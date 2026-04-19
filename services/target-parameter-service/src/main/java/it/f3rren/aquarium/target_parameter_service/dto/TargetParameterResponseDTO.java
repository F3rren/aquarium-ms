package it.f3rren.aquarium.target_parameter_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetParameterResponseDTO {
    private Long id;
    private Long aquariumId;
    private Double temperature;
    private Double ph;
    private Double salinity;
    private Double orp;
}
