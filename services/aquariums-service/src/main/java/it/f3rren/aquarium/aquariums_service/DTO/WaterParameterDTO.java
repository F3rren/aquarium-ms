package it.f3rren.aquarium.aquariums_service.dto;

import java.time.LocalDateTime;
import lombok.*;

/**
 * DTO for a single water parameter measurement.
 *
 * <p>Used both as the request body when recording a new measurement and as the
 * response payload when reading existing ones. The {@code aquariumId} field is
 * always set server-side (injected from the path variable) and must not be
 * supplied by the caller.</p>
 *
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterParameterDTO {

    /** Server-generated identifier. Populated in responses, ignored on creation. */
    private Long id;

    /**
     * ID of the aquarium this measurement belongs to.
     * Injected from the URL path by {@code ParametersClient} — callers should not set this.
     */
    private Long aquariumId;

    /** Water temperature in degrees Celsius. */
    private Double temperature;

    /** pH level of the water (typical range 6.5–8.5). */
    private Double ph;

    /** Salinity in milligrams per liter (mg/L). Relevant for saltwater aquariums. */
    private Double salinity;

    /** Date and time when the measurement was taken. */
    private LocalDateTime measuredAt;
}
