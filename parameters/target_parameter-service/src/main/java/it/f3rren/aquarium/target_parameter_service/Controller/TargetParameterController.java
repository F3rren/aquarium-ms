package it.f3rren.aquarium.target_parameter_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.target_parameter_service.model.TargetParameter;
import it.f3rren.aquarium.target_parameter_service.service.TargetParameterService;

@RestController
@RequestMapping("/aquariums/{aquariumId}/settings/targets")
@Tag(name = "TargetParameter", description = "API for managing target parameters")
public class TargetParameterController {
    
    @Autowired
    private TargetParameterService targetParameterService;

    @GetMapping
    @Operation(summary = "Get target parameters for an aquarium", description = "Retrieve target parameters for a specific aquarium")
    public ResponseEntity<?> getTargetParameters(@PathVariable Long aquariumId) {
        TargetParameter targets = targetParameterService.getTargetParameters(aquariumId);
        
        if (targets == null) {
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "No custom target parameter found",
                "data", (Object) null
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Target parameters retrieved successfully",
            "data", targets
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Save target parameters for an aquarium", description = "Save or update target parameters for a specific aquarium")
    public ResponseEntity<?> saveTargetParameters(
            @PathVariable Long aquariumId,
            @RequestBody TargetParameter targetParameter) {
        
        TargetParameter saved = targetParameterService.saveTargetParameters(aquariumId, targetParameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Target parameters saved successfully",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
