package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * DTO for a manual chemical parameter measurement.
 *
 * <p>Manual parameters represent values that require a test kit to measure
 * (calcium, magnesium, KH, nitrate, phosphate). Used both as the request body
 * when recording a new measurement and as the response payload when reading
 * existing ones. The {@code aquariumId} field is always set server-side.</p>
 *
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualParameterDTO {

    /** Server-generated identifier. Populated in responses, ignored on creation. */
    private Long id;

    /**
     * ID of the aquarium this measurement belongs to.
     * Injected from the URL path by {@code ParametersClient} — callers should not set this.
     */
    private Long aquariumId;

    /** Calcium concentration in mg/L. Must be positive if provided. */
    @Positive(message = "Calcium concentration must be a positive number")
    private Double calcium;

    /** Magnesium concentration in mg/L. Must be positive if provided. */
    @Positive(message = "Magnesium concentration must be a positive number")
    private Double magnesium;

    /** Carbonate hardness (KH) in dKH. Must be positive if provided. */
    @Positive(message = "KH concentration must be a positive number")
    private Double kh;

    /** Nitrate concentration in mg/L. Must be positive if provided. */
    @Positive(message = "Nitrate concentration must be a positive number")
    private Double nitrate;

    /** Phosphate concentration in mg/L. Must be positive if provided. */
    @Positive(message = "Phosphate concentration must be a positive number")
    private Double phosphate;

    /** Date and time when the measurement was taken. Required. */
    @NotNull(message = "Measured at date cannot be null")
    private LocalDateTime measuredAt;

    /** Optional free-text notes about the measurement (e.g. dosing actions taken). */
    private String notes;
}
