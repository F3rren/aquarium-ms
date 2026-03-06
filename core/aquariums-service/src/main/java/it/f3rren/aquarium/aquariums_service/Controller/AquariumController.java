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

/**
 * Controller for managing aquariums. Provides endpoints for CRUD operations on aquariums.
 * This controller handles HTTP requests related to aquarium management, including creating, retrieving, updating, and deleting aquariums.
 * It uses the AquariumService to perform business logic operations on aquariums.
 * It also uses the AquariumService to perform business logic operations on aquariums.
 * @author f3rren
 */
@RestController
@RequestMapping("/aquariums")
@Tag(name = "Aquarium", description = "API for managing aquariums")
public class AquariumController {

    /**
     * Service for managing aquariums data.
     */
    private final AquariumService aquariumService;

    /**
     * Constructor for AquariumController. Initializes the AquariumService dependency.
     * @param aquariumService AquariumService instance for aquarium operations
     */
    public AquariumController(AquariumService aquariumService) {
        this.aquariumService = aquariumService;
    }

    /**
     * Retrieves all aquariums.
     * @return ResponseEntity containing a list of aquariums and a success message or an error message if the operation fails.
     */
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

    /**
     * Retrieves an aquarium by its ID.
     * @param id ID of the aquarium to retrieve
     * @return ResponseEntity containing aquarium details and a success message or an error message if the operation fails.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get aquarium by ID", description = "Retrieve details of a specific aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> getAquariumById(@PathVariable Long id) {
        Aquarium aquarium = aquariumService.getAquariumById(id);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Aquarium retrieved successfully",
                        AquariumResponseDTO.fromEntity(aquarium), null));
    }

    /**
     * Creates a new aquarium.
     * @param dto Aquarium details to be created
     * @return ResponseEntity containing created aquarium details and a success message or an error message if the operation fails.
     */
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

    /**
     * Updates an existing aquarium.
     * @param id ID of the aquarium to update
     * @param dto Updated aquarium details
     * @return ResponseEntity containing updated aquarium details and a success message or an error message if the operation fails.
     */
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

    /**
     * Deletes an aquarium by its ID.
     * @param id ID of the aquarium to delete
     * @return ResponseEntity containing a success message or an error message if the operation fails.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an aquarium", description = "Remove a specific aquarium")
    public ResponseEntity<ApiResponseDTO<Void>> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Aquarium deleted successfully", null, null));
    }
}
