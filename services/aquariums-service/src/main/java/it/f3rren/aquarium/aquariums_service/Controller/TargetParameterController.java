package it.f3rren.aquarium.aquariums_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.aquariums_service.client.ParametersClient;
import it.f3rren.aquarium.aquariums_service.dto.*;
import jakarta.validation.Valid;

/**
 * Proxy controller for target parameter endpoints.
 * Delegates all operations to the parameters microservice via {@link ParametersClient}.
 *
 * @author F3rren
 */
@RestController
@RequestMapping("/aquariums")
@Tag(name = "Target Parameters", description = "Proxy endpoints for target parameter values")
public class TargetParameterController {

    private final ParametersClient parametersClient;

    public TargetParameterController(ParametersClient parametersClient) {
        this.parametersClient = parametersClient;
    }

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
