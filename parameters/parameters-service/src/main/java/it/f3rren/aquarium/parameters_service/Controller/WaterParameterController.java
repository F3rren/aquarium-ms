package it.f3rren.aquarium.parameters_service.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.parameters_service.Model.Parameter;
import it.f3rren.aquarium.parameters_service.Service.ParameterService;

@RestController
@RequestMapping("/api/water-parameters")
@Tag(name = "WaterParameter", description = "API for managing water parameters")
public class WaterParameterController {
    
    @Autowired
    private ParameterService parameterService;

    @PostMapping
    @Operation(summary = "Add a new water parameter", description = "Add a new water parameter for a specific aquarium")
    public ResponseEntity<?> addParameter(@RequestBody Parameter parameter) {
        Parameter saved = parameterService.saveParameter(parameter.getAquariumId(), parameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parameter saved successfully",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/aquarium/{aquariumId}")
    @Operation(summary = "Get water parameters for an aquarium", description = "Retrieve water parameters for a specific aquarium with an optional limit")
    public ResponseEntity<?> getParametersByAquarium(
            @PathVariable Long aquariumId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<Parameter> parameters = parameterService.getParametersByAquariumId(aquariumId, limit);

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parameters retrieved successfully",
            "data", parameters,
            "metadata", Map.of(
                "aquariumId", aquariumId,
                "count", parameters.size(),
                "limit", limit
            )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/aquarium/{aquariumId}/latest")
    @Operation(summary = "Get the latest water parameter for an aquarium", description = "Retrieve the most recent water parameter for a specific aquarium")
    public ResponseEntity<?> getLatestParameter(@PathVariable Long aquariumId) {
        Parameter latest = parameterService.getLatestParameter(aquariumId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Latest parameter retrieved successfully",
            "data", latest
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/aquarium/{aquariumId}/history")
    @Operation(summary = "Get water parameters history for an aquarium", description = "Retrieve water parameters history for a specific aquarium based on period or date range")
    public ResponseEntity<?> getParametersHistory(
            @PathVariable Long aquariumId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        
        List<Parameter> parameters;
        
        if (period != null) {
            parameters = parameterService.getParametersByPeriod(aquariumId, period);
        } else if (from != null && to != null) {
            LocalDateTime fromDate = LocalDateTime.parse(from);
            LocalDateTime toDate = LocalDateTime.parse(to);
            parameters = parameterService.getParametersHistory(aquariumId, fromDate, toDate);
        } else {
            parameters = parameterService.getParametersByPeriod(aquariumId, "week");
        }

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parameters history retrieved successfully",
            "data", parameters
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
