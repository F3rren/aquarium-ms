package it.f3rren.aquarium.parameters_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import it.f3rren.aquarium.parameters_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.dto.ParameterDTO;
import it.f3rren.aquarium.parameters_service.service.IParameterService;

@RestController
@RequestMapping("/aquariums/{id}/parameters")
@Tag(name = "WaterParameter", description = "API for managing water parameters")
public class WaterParameterController {

    private final IParameterService parameterService;

    public WaterParameterController(IParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @PostMapping
    @Operation(summary = "Add a new water parameter", description = "Add a new water parameter for a specific aquarium")
    public ResponseEntity<ApiResponseDTO<ParameterDTO>> addParameter(
            @PathVariable Long id,
            @Valid @RequestBody CreateParameterDTO dto) {

        ParameterDTO saved = parameterService.saveParameter(id, dto);

        return new ResponseEntity<>(new ApiResponseDTO<>(
                true, "Parameter saved successfully", saved, null), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get water parameters", description = "Retrieve water parameters for a specific aquarium with optional limit")
    public ResponseEntity<ApiResponseDTO<List<ParameterDTO>>> getParametersByAquarium(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {

        List<ParameterDTO> parameters = parameterService.getParametersByAquariumId(id, limit);

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true, "Parameters retrieved successfully", parameters,
                Map.of("aquariumId", id, "count", parameters.size(), "limit", limit)));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest water parameter", description = "Retrieve the most recent water parameter")
    public ResponseEntity<ApiResponseDTO<ParameterDTO>> getLatestParameter(@PathVariable Long id) {
        ParameterDTO latest = parameterService.getLatestParameter(id);

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true, "Latest parameter retrieved successfully", latest, null));
    }

    @GetMapping("/history")
    @Operation(summary = "Get water parameters history", description = "Retrieve history based on period or date range")
    public ResponseEntity<ApiResponseDTO<List<ParameterDTO>>> getParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        List<ParameterDTO> parameters;

        if (period != null) {
            parameters = parameterService.getParametersByPeriod(id, period);
        } else if (from != null && to != null) {
            // DateTimeParseException is handled by GlobalExceptionHandler → 400 Bad Request
            LocalDateTime fromDate = LocalDateTime.parse(from);
            LocalDateTime toDate = LocalDateTime.parse(to);
            parameters = parameterService.getParametersHistory(id, fromDate, toDate);
        } else {
            parameters = parameterService.getParametersByPeriod(id, "week");
        }

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true, "Parameters history retrieved successfully", parameters, null));
    }
}
