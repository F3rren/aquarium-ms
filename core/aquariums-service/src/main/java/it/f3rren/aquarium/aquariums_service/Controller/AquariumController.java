package it.f3rren.aquarium.aquariums_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.aquariums_service.client.ParametersClient;
import it.f3rren.aquarium.aquariums_service.dto.*;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.service.IAquariumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller for managing aquariums and their parameters.
 * Provides CRUD endpoints for aquariums and proxy endpoints for
 * water, manual, and target parameters via inter-service communication.
 * 
 * @author f3rren
 */
@RestController
@RequestMapping("/aquariums")
@Tag(name = "Aquarium", description = "API for managing aquariums and their parameters")
public class AquariumController {

    private final IAquariumService aquariumService;
    private final ParametersClient parametersClient;

    /**
     * Constructor for AquariumController.
     *
     * @param aquariumService  service for aquarium CRUD operations
     * @param parametersClient client for inter-service parameter operations
     */
    public AquariumController(IAquariumService aquariumService, ParametersClient parametersClient) {
        this.aquariumService = aquariumService;
        this.parametersClient = parametersClient;
    }

    // ========================
    // Aquarium CRUD
    // ========================

    /**
     * Retrieves all aquariums.
     * 
     * @return ResponseEntity containing a list of aquariums
     */
    @GetMapping
    @Operation(summary = "Get all aquariums", description = "Retrieve details of all aquariums")
    public ResponseEntity<ApiResponseDTO<List<AquariumResponseDTO>>> getAllAquariums() {
        List<AquariumResponseDTO> aquariums = aquariumService.getAllAquariums()
                .stream()
                .map(AquariumResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(ApiResponseDTO.success("Aquariums retrieved successfully", aquariums));
    }

    /**
     * Retrieves an aquarium by its ID.
     * 
     * @param id ID of the aquarium to retrieve
     * @return ResponseEntity containing aquarium details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get aquarium by ID", description = "Retrieve details of a specific aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> getAquariumById(@PathVariable Long id) {
        Aquarium aquarium = aquariumService.getAquariumById(id);

        return ResponseEntity.ok(ApiResponseDTO.success("Aquarium retrieved successfully",
                        AquariumResponseDTO.fromEntity(aquarium)));
    }

    /**
     * Creates a new aquarium.
     * 
     * @param dto Aquarium details to be created
     * @return ResponseEntity containing created aquarium details
     */
    @PostMapping
    @Operation(summary = "Create a new aquarium", description = "Receive and save a new aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> createAquarium(
            @Valid @RequestBody CreateAquariumDTO dto) {
        Aquarium savedAquarium = aquariumService.createAquarium(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Aquarium created successfully",
                        AquariumResponseDTO.fromEntity(savedAquarium)));
    }

    /**
     * Updates an existing aquarium.
     * 
     * @param id  ID of the aquarium to update
     * @param dto Updated aquarium details
     * @return ResponseEntity containing updated aquarium details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing aquarium", description = "Modify details of a specific aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> updateAquarium(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAquariumDTO dto) {
        Aquarium updatedAquarium = aquariumService.updateAquarium(id, dto);

        return ResponseEntity.ok(ApiResponseDTO.success("Aquarium updated successfully",
                        AquariumResponseDTO.fromEntity(updatedAquarium)));
    }

    /**
     * Deletes an aquarium by its ID.
     * 
     * @param id ID of the aquarium to delete
     * @return ResponseEntity containing a success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an aquarium", description = "Remove a specific aquarium")
    public ResponseEntity<ApiResponseDTO<Void>> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);

        return ResponseEntity.ok(ApiResponseDTO.success("Aquarium deleted successfully"));
    }

    // ========================
    // Water Parameters
    // ========================

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
        parameter.setAquariumId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(parametersClient.addWaterParameter(parameter));
    }

    /**
     * Retrieves water parameters for an aquarium.
     * 
     * @param id    Aquarium ID
     * @param limit Maximum number of results
     * @return ApiResponseDTO with the list of water parameters
     */
    @GetMapping("/{id}/water-parameters")
    @Operation(summary = "Get water parameters", description = "Retrieve water parameter measurements for an aquarium")
    public ResponseEntity<ApiResponseDTO<List<WaterParameterDTO>>> getWaterParameters(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") Integer limit) {
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
     * @param period Time period (e.g. "day", "week", "month")
     * @param from   Start date
     * @param to     End date
     * @return ApiResponseDTO with the history
     */
    @GetMapping("/{id}/water-parameters/history")
    @Operation(summary = "Get water parameters history", description = "Retrieve historical water parameter data")
    public ResponseEntity<ApiResponseDTO<List<WaterParameterDTO>>> getWaterParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(parametersClient.getWaterParametersHistory(id, period, from, to));
    }

    // ========================
    // Manual Parameters
    // ========================

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
        parameter.setAquariumId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(parametersClient.addManualParameter(parameter));
    }

    /**
     * Retrieves all manual parameters for an aquarium.
     * 
     * @param id Aquarium ID
     * @return ApiResponseDTO with the list of manual parameters
     */
    @GetMapping("/{id}/manual-parameters")
    @Operation(summary = "Get manual parameters", description = "Retrieve all manual parameter measurements for an aquarium")
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
     * Retrieves manual parameters history for an aquarium.
     * 
     * @param id   Aquarium ID
     * @param from Start date
     * @param to   End date
     * @return ApiResponseDTO with the history
     */
    @GetMapping("/{id}/manual-parameters/history")
    @Operation(summary = "Get manual parameters history", description = "Retrieve historical manual parameter data")
    public ResponseEntity<ApiResponseDTO<List<ManualParameterDTO>>> getManualParametersHistory(
            @PathVariable Long id,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(parametersClient.getManualParametersHistory(id, from, to));
    }

    // ========================
    // Target Parameters
    // ========================

    /**
     * Retrieves target parameters for an aquarium.
     * 
     * @param id Aquarium ID
     * @return ApiResponseDTO with the target parameters
     */
    @GetMapping("/{id}/target-parameters")
    @Operation(summary = "Get target parameters", description = "Retrieve the target parameter values for an aquarium")
    public ResponseEntity<ApiResponseDTO<TargetParameterDTO>> getTargetParameters(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getTargetParameters(id));
    }

    /**
     * Saves target parameters for an aquarium.
     * 
     * @param id              Aquarium ID
     * @param targetParameter Target parameter values
     * @return ApiResponseDTO with the saved target parameters
     */
    @PostMapping("/{id}/target-parameters")
    @Operation(summary = "Save target parameters", description = "Set target parameter values for an aquarium")
    public ResponseEntity<ApiResponseDTO<TargetParameterDTO>> saveTargetParameters(
            @PathVariable Long id,
            @Valid @RequestBody TargetParameterDTO targetParameter) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parametersClient.saveTargetParameters(id, targetParameter));
    }
}
