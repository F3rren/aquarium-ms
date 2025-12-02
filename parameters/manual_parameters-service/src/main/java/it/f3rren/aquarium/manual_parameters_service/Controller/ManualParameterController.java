package it.f3rren.aquarium.manual_parameters_service.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.manual_parameters_service.Model.ManualParameter;
import it.f3rren.aquarium.manual_parameters_service.Service.ManualParameterService;

@RestController
@RequestMapping("/api/manual-parameters")
@Tag(name = "ManualParameter", description = "API for managing manual parameters")
public class ManualParameterController {
    
    @Autowired
    private ManualParameterService manualParameterService;

    @PostMapping
    @Operation(summary = "Add a new manual parameter", description = "Add a new manual parameter for a specific aquarium")
    public ResponseEntity<?> addManualParameter(@RequestBody ManualParameter parameter) {
        ManualParameter saved = manualParameterService.saveManualParameter(parameter.getAquariumId(), parameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Manual parameter saved successfully",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/aquarium/{aquariumId}/latest")
    @Operation(summary = "Get the latest manual parameter for an aquarium", description = "Retrieve the most recent manual parameter for a specific aquarium")
    public ResponseEntity<?> getLatestManualParameter(@PathVariable Long aquariumId) {
        ManualParameter latest = manualParameterService.getLatestManualParameter(aquariumId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Latest manual parameter retrieved successfully",
            "data", latest
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/aquarium/{aquariumId}")
    @Operation(summary = "Get all manual parameters for an aquarium", description = "Retrieve all manual parameters for a specific aquarium")
    public ResponseEntity<?> getAllManualParameters(@PathVariable Long aquariumId) {
        List<ManualParameter> parameters = manualParameterService.getAllManualParameters(aquariumId);

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Manual parameters retrieved successfully",
            "data", parameters,
            "metadata", Map.of(
                "aquariumId", aquariumId,
                "count", parameters.size()
            )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/aquarium/{aquariumId}/history")
    @Operation(summary = "Get manual parameters history for an aquarium", description = "Retrieve manual parameters for a specific aquarium within a date range")
    public ResponseEntity<?> getManualParametersHistory(
            @PathVariable Long aquariumId,
            @RequestParam String from,
            @RequestParam String to) {
        
        LocalDateTime fromDate = LocalDateTime.parse(from);
        LocalDateTime toDate = LocalDateTime.parse(to);
        
        List<ManualParameter> parameters = manualParameterService.getManualParametersHistory(
            aquariumId, fromDate, toDate
        );

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Manual parameters history retrieved successfully",
            "data", parameters
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
