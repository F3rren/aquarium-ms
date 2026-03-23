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
import it.f3rren.aquarium.species.model.Fish;
import it.f3rren.aquarium.species.service.IFishService;

@RestController
@RequestMapping("/species/fish")
@Tag(name = "Fish", description = "API for managing fish")
public class FishController {

    private final IFishService fishService;

    public FishController(IFishService fishService) {
        this.fishService = fishService;
    }

    @GetMapping
    @Operation(summary = "Get all fish", description = "Retrieve a list of all fish species")
    public ResponseEntity<ApiResponseDTO<List<Fish>>> getAllFish() {
        List<Fish> fish = fishService.getAllFish();

        ApiResponseDTO<List<Fish>> response = new ApiResponseDTO<>(
                true,
                "Fish retrieved successfully",
                fish,
                Map.of("count", fish.size()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a fish by ID", description = "Retrieve details of a specific fish by its ID")
    public ResponseEntity<ApiResponseDTO<Fish>> getFishById(@PathVariable Long id) {
        Fish fish = fishService.getFishById(id);

        ApiResponseDTO<Fish> response = new ApiResponseDTO<>(
                true,
                "Fish retrieved successfully",
                fish,
                null);

        return ResponseEntity.ok(response);
    }
}
