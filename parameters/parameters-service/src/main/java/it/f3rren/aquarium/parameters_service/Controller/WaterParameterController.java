package it.f3rren.aquarium.parameters_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.parameters_service.model.Parameter;
import it.f3rren.aquarium.parameters_service.service.ParameterService;

@RestController
@RequestMapping("/aquariums/{id}/parameters")
@Tag(name = "WaterParameter", description = "API for managing water parameters")
public class WaterParameterController {
    
    @Autowired
    private ParameterService parameterService;

    @PostMapping
    @Operation(summary = "Add a new water parameter", description = "Add a new water parameter for a specific aquarium")
    public ResponseEntity<?> addParameter(@PathVariable Long id, @RequestBody Parameter parameter) {
        Parameter saved = parameterService.saveParameter(id, parameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parameter saved successfully",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get water parameters for an aquarium", description = "Retrieve water parameters for a specific aquarium with an optional limit")
    public ResponseEntity<?> getParametersByAquarium(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<Parameter> parameters = parameterService.getParametersByAquariumId(id, limit);

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parameters retrieved successfully",
            "data", parameters,
            "metadata", Map.of(
                "aquariumId", id,
                "count", parameters.size(),
                "limit", limit
            )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the latest water parameter for an aquarium", description = "Retrieve the most recent water parameter for a specific aquarium")
    public ResponseEntity<?> getLatestParameter(@PathVariable Long id) {
        Parameter latest = parameterService.getLatestParameter(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Latest parameter retrieved successfully",
            "data", latest
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/history")
    @Operation(summary = "Get water parameters history for an aquarium", description = "Retrieve water parameters history for a specific aquarium based on period or date range")
    public ResponseEntity<?> getParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        
        List<Parameter> parameters;
        
        if (period != null) {
            parameters = parameterService.getParametersByPeriod(id, period);
        } else if (from != null && to != null) {
            LocalDateTime fromDate = LocalDateTime.parse(from);
            LocalDateTime toDate = LocalDateTime.parse(to);
            parameters = parameterService.getParametersHistory(id, fromDate, toDate);
        } else {
            parameters = parameterService.getParametersByPeriod(id, "week");
        }

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parameters history retrieved successfully",
            "data", parameters
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
