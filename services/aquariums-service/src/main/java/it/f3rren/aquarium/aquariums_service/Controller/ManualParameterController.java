package it.f3rren.aquarium.aquariums_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.aquariums_service.client.ParametersClient;
import it.f3rren.aquarium.aquariums_service.dto.*;
import jakarta.validation.Valid;

/**
 * Proxy controller for manual parameter endpoints.
 * Delegates all operations to the parameters microservice via {@link ParametersClient}.
 *
 * @author F3rren
 */
@RestController
@RequestMapping("/aquariums")
@Tag(name = "Manual Parameters", description = "Proxy endpoints for manual parameter measurements")
public class ManualParameterController {

    private final ParametersClient parametersClient;

    public ManualParameterController(ParametersClient parametersClient) {
        this.parametersClient = parametersClient;
    }

    /**
     * Adds a manual parameter measurement for an aquarium.
     *
     * @param id        Aquarium ID
     * @param parameter Manual parameter data
     * @return ApiResponseDTO with the added parameter
     */
    @PostMapping("/{id}/manual-parameters")
    @Operation(summary = "Add manual parameter", description = "Record a new manual parameter measurement for an aquarium")
    public ResponseEntity<ApiResponseDTO<ManualParameterDTO>> addManualParameter(
            @PathVariable Long id,
            @Valid @RequestBody ManualParameterDTO parameter) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parametersClient.addManualParameter(id, parameter));
    }

    /**
     * Retrieves all manual parameters for an aquarium.
     *
     * @param id Aquarium ID
     * @return ApiResponseDTO with the list of manual parameters
     */
    @GetMapping("/{id}/manual-parameters")
    @Operation(summary = "Get manual parameters", description = "Retrieve all manual parameter measurements for an aquarium")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTOManualParameterList.class)))
    public ResponseEntity<ApiResponseDTO<List<ManualParameterDTO>>> getManualParameters(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getAllManualParameters(id));
    }

    /**
     * Retrieves the latest manual parameter for an aquarium.
     *
     * @param id Aquarium ID
     * @return ApiResponseDTO with the latest manual parameter
     */
    @GetMapping("/{id}/manual-parameters/latest")
    @Operation(summary = "Get latest manual parameter", description = "Retrieve the most recent manual parameter measurement")
    public ResponseEntity<ApiResponseDTO<ManualParameterDTO>> getLatestManualParameter(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getLatestManualParameter(id));
    }

    /**
     * Retrieves manual parameters history for an aquarium within a date range.
     *
     * @param id   Aquarium ID
     * @param from Start datetime (ISO-8601); Spring converts automatically
     * @param to   End datetime (ISO-8601); Spring converts automatically
     * @return ApiResponseDTO with the history
     */
    @GetMapping("/{id}/manual-parameters/history")
    @Operation(summary = "Get manual parameters history", description = "Retrieve historical manual parameter data")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTOManualParameterList.class)))
    public ResponseEntity<ApiResponseDTO<List<ManualParameterDTO>>> getManualParametersHistory(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(parametersClient.getManualParametersHistory(id, from.toString(), to.toString()));
    }
}
