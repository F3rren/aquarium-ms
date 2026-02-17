package it.f3rren.aquarium.target_parameter_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveTargetParameterDTO {

    private Double temperature;
    private Double ph;
    private Double salinity;
    private Double orp;
}
