package it.f3rren.aquarium.species.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.species.dto.ApiResponseDTO;
import it.f3rren.aquarium.species.model.Coral;
import it.f3rren.aquarium.species.service.CoralService;

@RestController
@RequestMapping("/species/corals")
@Tag(name = "Coral", description = "API for managing corals")
public class CoralController {

    private final CoralService coralService;

    public CoralController(CoralService coralService) {
        this.coralService = coralService;
    }

    @GetMapping
    @Operation(summary = "Get all corals", description = "Retrieve a list of all coral species")
    public ResponseEntity<ApiResponseDTO<List<Coral>>> getAllCorals() {
        List<Coral> corals = coralService.getAllCorals();

        ApiResponseDTO<List<Coral>> response = new ApiResponseDTO<>(
                true,
                "Corals retrieved successfully",
                corals,
                Map.of("count", corals.size()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a coral by ID", description = "Retrieve details of a specific coral by its ID")
    public ResponseEntity<ApiResponseDTO<Coral>> getCoralById(@PathVariable Long id) {
        Coral coral = coralService.getCoralById(id);

        ApiResponseDTO<Coral> response = new ApiResponseDTO<>(
                true,
                "Coral retrieved successfully",
                coral,
                null);

        return ResponseEntity.ok(response);
    }
}
