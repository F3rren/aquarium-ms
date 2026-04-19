package it.f3rren.aquarium.target_parameter_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.target_parameter_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.dto.TargetParameterResponseDTO;
import it.f3rren.aquarium.target_parameter_service.service.ITargetParameterService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/aquariums/{aquariumId}/settings/targets")
@Tag(name = "TargetParameter", description = "API for managing target parameters")
public class TargetParameterController {

    private final ITargetParameterService targetParameterService;

    public TargetParameterController(ITargetParameterService targetParameterService) {
        this.targetParameterService = targetParameterService;
    }

    @GetMapping
    @Operation(summary = "Get target parameters for an aquarium", description = "Retrieve target parameters for a specific aquarium")
    public ResponseEntity<ApiResponseDTO<TargetParameterResponseDTO>> getTargetParameters(@PathVariable Long aquariumId) {
        TargetParameterResponseDTO targets = targetParameterService.getTargetParameters(aquariumId);

        if (targets == null) {
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "No custom target parameter found", null, null));
        }

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Target parameters retrieved successfully", targets, null));
    }

    @PostMapping
    @Operation(summary = "Save target parameters for an aquarium", description = "Save or update target parameters for a specific aquarium")
    public ResponseEntity<ApiResponseDTO<TargetParameterResponseDTO>> saveTargetParameters(
            @PathVariable Long aquariumId,
            @Valid @RequestBody SaveTargetParameterDTO dto) {

        TargetParameterResponseDTO saved = targetParameterService.saveTargetParameters(aquariumId, dto);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Target parameters saved successfully", saved, null));
    }
}
