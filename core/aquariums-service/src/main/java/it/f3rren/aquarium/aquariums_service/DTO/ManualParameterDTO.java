package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;
import lombok.*;

/**
 * Data Transfer Object for manual parameters of an aquarium.
 * This class is used to transfer data between the application and the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualParameterDTO {

    /**
     * Manual parameter ID. This field is required and must be a positive number.
     * The default value is 0.
     */
    @Positive(message = "Manual parameter ID must be a positive number")
    private Long id;

    /**
     * Aquarium ID associated with the manual parameter. This field is required and must be a positive number.
     * The default value is 0.
     */
    @Positive(message = "Aquarium ID must be a positive number")
    private Long aquariumId;

    /**
     * Calcium concentration in the aquarium. This field is required and must be a positive number.
     * The default value is 0.
     */
    @Positive(message = "Calcium concentration must be a positive number")
    private Double calcium;

    /**
     * Magnesium concentration in the aquarium. This field is required and must be a positive number.
     * The default value is 0.
     */
    @Positive(message = "Magnesium concentration must be a positive number")
    private Double magnesium;

    /**
     * KH (Carbonate Hardness) concentration in the aquarium. This field is required and must be a positive number.
     * The default value is 0.
     */
    @Positive(message = "KH concentration must be a positive number")
    private Double kh;

    /**
     * Nitrate concentration in the aquarium. This field is required and must be a positive number.
     * The default value is 0.
     */
    @Positive(message = "Nitrate concentration must be a positive number")
    private Double nitrate;

    /**
     * Phosphate concentration in the aquarium. This field is required and must be a positive number.
     * The default value is 0.
     */
    @Positive(message = "Phosphate concentration must be a positive number")
    private Double phosphate;

    /**
     * Date and time when the manual parameter was measured. This field is required.
     * The default value is the current date and time. This field is optional.
     */
    @NotNull(message = "Measured at date cannot be null")
    private LocalDateTime measuredAt;

    /**
     * Notes about the manual parameter measurement. This field is optional.
     */
    private String notes;
}
