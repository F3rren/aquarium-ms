package it.f3rren.aquarium.maintenance_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuantityChangeDTO {

    @NotNull(message = "Change value is required")
    private Double change;
}
