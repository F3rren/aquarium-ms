package it.f3rren.aquarium.aquariums_service.dto;

import lombok.*;

/**
 * Data Transfer Object for target parameters.
 * Represents the ideal parameter values set as targets for an aquarium.
 * Used by ParametersClient to communicate with the target-parameters microservice.
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetParameterDTO {

    /**
     * Unique identifier for this target parameter record.
     */
    private Long id;

    /**
     * ID of the aquarium to which these target parameters are associated.
     */
    private Long aquariumId;

    /**
     * Target temperature level for the aquarium (°C).
     */
    private Double temperature;

    /**
     * Target pH level for the aquarium.
     */
    private Double ph;

    /**
     * Target salinity level for the aquarium.
     */
    private Double salinity;

    /**
     * Target ORP (Oxidation-Reduction Potential) level for the aquarium.
     */
    private Double orp;

    /**
     * Target calcium level for the aquarium (mg/L).
     */
    private Double calcium;

    /**
     * Target magnesium level for the aquarium (mg/L).
     */
    private Double magnesium;

    /**
     * Target KH (Carbonate Hardness) level for the aquarium (dKH).
     */
    private Double kh;

    /**
     * Target nitrate level for the aquarium (mg/L).
     */
    private Double nitrate;

    /**
     * Target phosphate level for the aquarium (mg/L).
     */
    private Double phosphate;
}
