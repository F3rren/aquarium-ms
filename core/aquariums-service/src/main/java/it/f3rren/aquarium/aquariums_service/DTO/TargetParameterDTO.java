package it.f3rren.aquarium.aquariums_service.dto;

import lombok.*;

/**
 * Data Transfer Object for target parameters.
 * This class represents the parameters that are set as targets for an aquarium.
 * It includes fields for each parameter and their respective values.
 * It also includes a constructor for initializing all fields.
 * Additionally, it includes getters, setters, and a no-argument constructor for Lombok to generate.
 * The @Data annotation is used to include all the annotations automatically.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to generate constructors.
 * The @Getter and @Setter annotations are used to generate getters and setters.
 * The @Builder annotation is used to generate a builder pattern for this class.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetParameterDTO {

    /**
     * ID field, represents the unique identifier for this target parameter.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Long id;

    /**
     * Aquarium ID field, represents the ID of the aquarium to which these parameters are associated.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Long aquariumId;

    /**
     * Temperature field, represents the target temperature level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double temperature;

    /**
     * Ph field, represents the target pH level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double ph;

    /**
     * Salinity field, represents the target salinity level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double salinity;

    /**
     * Ammonia field, represents the target ammonia level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double orp;

    /**
     * Calcium field, represents the target calcium level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double calcium;

    /**
     * Magnesium field, represents the target magnesium level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double magnesium;

    /**
     * KH field, represents the target KH level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double kh;

    /**
     * Nitrate field, represents the target nitrate level for the aquarium.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double nitrate;

    /**
     * Phosphate field, not used for now but could be added in the future.
     * @deprecated This field is deprecated and not used anymore.
     */
    @Deprecated
    private Double phosphate;
}
