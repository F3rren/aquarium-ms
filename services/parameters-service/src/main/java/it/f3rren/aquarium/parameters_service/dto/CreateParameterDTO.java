package it.f3rren.aquarium.parameters_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateParameterDTO {

    @NotNull(message = "Temperature is required")
    private Double temperature;

    @NotNull(message = "pH is required")
    private Double ph;

    @NotNull(message = "Salinity is required")
    @Positive(message = "Salinity must be positive")
    private Integer salinity;

    @NotNull(message = "ORP is required")
    private Integer orp;
}
