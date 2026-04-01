package it.f3rren.aquarium.manual_parameters_service.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManualParameterDTO {

    @Positive(message = "Calcium must be positive")
    private Double calcium;

    @Positive(message = "Magnesium must be positive")
    private Double magnesium;

    @Positive(message = "KH must be positive")
    private Double kh;

    private Double nitrate;

    private Double phosphate;

    @Size(max = 2000, message = "Notes must be at most 2000 characters")
    private String notes;
}
