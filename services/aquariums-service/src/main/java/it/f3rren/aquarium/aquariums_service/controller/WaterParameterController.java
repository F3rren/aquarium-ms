package it.f3rren.aquarium.aquariums_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.aquariums_service.client.ParametersClient;
import it.f3rren.aquarium.aquariums_service.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Proxy controller for water parameter endpoints.
 * Delegates all operations to the parameters microservice via {@link ParametersClient}.
 *
 * @author F3rren
 */
@RestController
@RequestMapping("/aquariums")
@Validated
@Tag(name = "Water Parameters", description = "Proxy endpoints for water parameter measurements")
public class WaterParameterController {

    private static final List<String> VALID_PERIODS = List.of("day", "week", "month");

    private final ParametersClient parametersClient;

    public WaterParameterController(ParametersClient parametersClient) {
        this.parametersClient = parametersClient;
    }

    /**
     * Adds a water parameter measurement for an aquarium.
     *
     * @param id        Aquarium ID
     * @param parameter Water parameter data
     * @return ApiResponseDTO with the added parameter
     */
    @PostMapping("/{id}/water-parameters")
    @Operation(summary = "Add water parameter", description = "Record a new water parameter measurement for an aquarium")
    public ResponseEntity<ApiResponseDTO<WaterParameterDTO>> addWaterParameter(
            @PathVariable Long id,
            @Valid @RequestBody WaterParameterDTO parameter) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parametersClient.addWaterParameter(id, parameter));
    }

    /**
     * Retrieves water parameters for an aquarium.
     *
     * @param id    Aquarium ID
     * @param limit Maximum number of results (1–100); validated via Bean Validation
     * @return ApiResponseDTO with the list of water parameters
     */
    @GetMapping("/{id}/water-parameters")
    @Operation(summary = "Get water parameters", description = "Retrieve water parameter measurements for an aquarium")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTOWaterParameterList.class)))
    public ResponseEntity<ApiResponseDTO<List<WaterParameterDTO>>> getWaterParameters(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return ResponseEntity.ok(parametersClient.getWaterParametersByAquarium(id, limit));
    }

    /**
     * Retrieves the latest water parameter for an aquarium.
     *
     * @param id Aquarium ID
     * @return ApiResponseDTO with the latest water parameter
     */
    @GetMapping("/{id}/water-parameters/latest")
    @Operation(summary = "Get latest water parameter", description = "Retrieve the most recent water parameter measurement")
    public ResponseEntity<ApiResponseDTO<WaterParameterDTO>> getLatestWaterParameter(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getLatestWaterParameter(id));
    }

    /**
     * Retrieves water parameters history for an aquarium.
     *
     * @param id     Aquarium ID
     * @param period Optional named period: "day", "week", or "month"
     * @param from   Optional start datetime (ISO-8601); Spring converts automatically
     * @param to     Optional end datetime (ISO-8601); Spring converts automatically
     * @return ApiResponseDTO with the history
     */
    @GetMapping("/{id}/water-parameters/history")
    @Operation(summary = "Get water parameters history", description = "Retrieve historical water parameter data")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTOWaterParameterList.class)))
    public ResponseEntity<ApiResponseDTO<List<WaterParameterDTO>>> getWaterParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        if (period != null && !VALID_PERIODS.contains(period)) {
            throw new IllegalArgumentException("Period must be one of: " + VALID_PERIODS);
        }
        return ResponseEntity.ok(parametersClient.getWaterParametersHistory(
                id, period,
                from != null ? from.toString() : null,
                to != null ? to.toString() : null));
    }
}
