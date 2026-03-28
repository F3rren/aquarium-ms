package it.f3rren.aquarium.manual_parameters_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import it.f3rren.aquarium.manual_parameters_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.model.ManualParameter;
import it.f3rren.aquarium.manual_parameters_service.service.IManualParameterService;

@RestController
@RequestMapping("/aquariums/{aquariumId}/parameters/manual")
@Tag(name = "ManualParameter", description = "API for managing manual parameters")
public class ManualParameterController {

    private final IManualParameterService manualParameterService;

    public ManualParameterController(IManualParameterService manualParameterService) {
        this.manualParameterService = manualParameterService;
    }

    @PostMapping
    @Operation(summary = "Add a new manual parameter", description = "Add a new manual parameter for a specific aquarium")
    public ResponseEntity<ApiResponseDTO<ManualParameter>> addManualParameter(
            @PathVariable Long aquariumId,
            @Valid @RequestBody CreateManualParameterDTO dto) {

        ManualParameter saved = manualParameterService.saveManualParameter(aquariumId, dto);

        return new ResponseEntity<>(new ApiResponseDTO<>(
                true, "Manual parameter saved successfully", saved, null), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get latest manual parameter", description = "Retrieve the most recent manual parameter")
    public ResponseEntity<ApiResponseDTO<ManualParameter>> getLatestManualParameter(
            @PathVariable Long aquariumId) {

        ManualParameter latest = manualParameterService.getLatestManualParameter(aquariumId);

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true, "Latest manual parameter retrieved successfully", latest, null));
    }

    @GetMapping("/history")
    @Operation(summary = "Get manual parameters history", description = "Retrieve manual parameters within a date range")
    public ResponseEntity<ApiResponseDTO<List<ManualParameter>>> getManualParametersHistory(
            @PathVariable Long aquariumId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        List<ManualParameter> parameters;

        if (from != null && to != null) {
            LocalDateTime fromDate = LocalDateTime.parse(from);
            LocalDateTime toDate = LocalDateTime.parse(to);
            parameters = manualParameterService.getManualParametersHistory(aquariumId, fromDate, toDate);
        } else {
            parameters = manualParameterService.getAllManualParameters(aquariumId);
        }

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true, "Manual parameters history retrieved successfully", parameters, null));
    }
}
