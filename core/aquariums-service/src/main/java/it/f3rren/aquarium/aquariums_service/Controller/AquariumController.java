package it.f3rren.aquarium.aquariums_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.AquariumResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.service.AquariumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/aquariums")
@Tag(name = "Aquarium", description = "API for managing aquariums")
public class AquariumController {

    private final AquariumService aquariumService;

    public AquariumController(AquariumService aquariumService) {
        this.aquariumService = aquariumService;
    }

    @GetMapping
    @Operation(summary = "Get all aquariums", description = "Retrieve details of all aquariums")
    public ResponseEntity<ApiResponseDTO<List<AquariumResponseDTO>>> getAllAquariums() {
        List<AquariumResponseDTO> aquariums = aquariumService.getAllAquariums()
                .stream()
                .map(AquariumResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Aquariums retrieved successfully", aquariums, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get aquarium by ID", description = "Retrieve details of a specific aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> getAquariumById(@PathVariable Long id) {
        Aquarium aquarium = aquariumService.getAquariumById(id);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Aquarium retrieved successfully",
                        AquariumResponseDTO.fromEntity(aquarium), null));
    }

    @PostMapping
    @Operation(summary = "Create a new aquarium", description = "Receive and save a new aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> createAquarium(
            @Valid @RequestBody CreateAquariumDTO dto) {
        Aquarium savedAquarium = aquariumService.createAquarium(dto);

        return new ResponseEntity<>(
                new ApiResponseDTO<>(true, "Aquarium created successfully",
                        AquariumResponseDTO.fromEntity(savedAquarium), null),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing aquarium", description = "Modify details of a specific aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> updateAquarium(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAquariumDTO dto) {
        Aquarium updatedAquarium = aquariumService.updateAquarium(id, dto);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Aquarium updated successfully",
                        AquariumResponseDTO.fromEntity(updatedAquarium), null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an aquarium", description = "Remove a specific aquarium")
    public ResponseEntity<ApiResponseDTO<Void>> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Aquarium deleted successfully", null, null));
    }
}
