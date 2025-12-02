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
@RequestMapping("/aquariums/{aquariumId}/parameters/manual")
@Tag(name = "ManualParameter", description = "API for managing manual parameters")
public class ManualParameterController {
    
    @Autowired
    private ManualParameterService manualParameterService;

    @PostMapping
    @Operation(summary = "Add a new manual parameter", description = "Add a new manual parameter for a specific aquarium")
    public ResponseEntity<?> addManualParameter(@PathVariable Long aquariumId, @RequestBody ManualParameter parameter) {
        parameter.setAquariumId(aquariumId);
        ManualParameter saved = manualParameterService.saveManualParameter(aquariumId, parameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Manual parameter saved successfully",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
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
    
    @GetMapping("/history")
    @Operation(summary = "Get manual parameters history for an aquarium", description = "Retrieve manual parameters for a specific aquarium within a date range")
    public ResponseEntity<?> getManualParametersHistory(
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

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Manual parameters history retrieved successfully",
            "data", parameters
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
