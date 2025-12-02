package it.f3rren.aquarium.species.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.species.Model.Coral;
import it.f3rren.aquarium.species.Service.CoralService;

@RestController
@RequestMapping("api/species/corals")
@Tag(name = "Coral", description = "API for managing corals")
public class CoralController {
 
    @Autowired
    public CoralService coralService;

    @GetMapping
    @Operation(summary = "Get all corals", description = "Retrieve a list of all corals")
    public ResponseEntity<?> getAllCorals() {
        List<Coral> corals = coralService.getAllCorals();
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Corals retrieved successfully",
            "data", corals
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a coral by ID", description = "Retrieve details of a specific coral by its ID")
    public ResponseEntity<?> getCoralById(@PathVariable Long id) {
        Coral coral = coralService.getCoralById(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Coral retrieved successfully",
            "data", coral
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
