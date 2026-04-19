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
import it.f3rren.aquarium.species.dto.CoralResponseDTO;
import it.f3rren.aquarium.species.dto.FishResponseDTO;
import it.f3rren.aquarium.species.service.ISpeciesService;

@RestController
@RequestMapping("/species")
@Tag(name = "Species", description = "API for managing fish and coral species")
public class SpeciesController {

    private final ISpeciesService speciesService;

    public SpeciesController(ISpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    @GetMapping("/fish")
    @Operation(summary = "Get all fish", description = "Retrieve a list of all fish species")
    public ResponseEntity<ApiResponseDTO<List<FishResponseDTO>>> getAllFish() {
        List<FishResponseDTO> fish = speciesService.getAllFish();

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Fish retrieved successfully",
                fish,
                Map.of("count", fish.size())));
    }

    @GetMapping("/fish/{id}")
    @Operation(summary = "Get a fish by ID", description = "Retrieve details of a specific fish by its ID")
    public ResponseEntity<ApiResponseDTO<FishResponseDTO>> getFishById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Fish retrieved successfully",
                speciesService.getFishById(id),
                null));
    }

    @GetMapping("/corals")
    @Operation(summary = "Get all corals", description = "Retrieve a list of all coral species")
    public ResponseEntity<ApiResponseDTO<List<CoralResponseDTO>>> getAllCorals() {
        List<CoralResponseDTO> corals = speciesService.getAllCorals();

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Corals retrieved successfully",
                corals,
                Map.of("count", corals.size())));
    }

    @GetMapping("/corals/{id}")
    @Operation(summary = "Get a coral by ID", description = "Retrieve details of a specific coral by its ID")
    public ResponseEntity<ApiResponseDTO<CoralResponseDTO>> getCoralById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Coral retrieved successfully",
                speciesService.getCoralById(id),
                null));
    }
}
