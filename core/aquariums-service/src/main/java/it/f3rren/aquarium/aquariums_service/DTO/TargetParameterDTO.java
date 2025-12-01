package it.f3rren.aquarium.aquariums_service.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetParameterDTO {
    private Long id;
    private Long aquariumId;
    private Double temperature;
    private Double ph;
    private Double salinity;
    private Double orp;
    private Double calcium;
    private Double magnesium;
    private Double kh;
    private Double nitrate;
    private Double phosphate;
}
