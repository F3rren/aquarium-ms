package it.f3rren.aquarium.aquariums_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.aquariums_service.dto.*;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.service.IAquariumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller for CRUD operations on aquariums.
 * Parameter proxy endpoints are handled by dedicated controllers:
 * {@link WaterParameterController}, {@link ManualParameterController}, {@link TargetParameterController}.
 *
 * @author f3rren
 */
@RestController
@RequestMapping("/aquariums")
@Tag(name = "Aquarium", description = "API for managing aquariums")
public class AquariumController {

    private final IAquariumService aquariumService;

    public AquariumController(IAquariumService aquariumService) {
        this.aquariumService = aquariumService;
    }

    /**
     * Retrieves aquariums with pagination. Defaults to page 0, size 20, sorted by id.
     *
     * @param pageable pagination and sorting parameters
     * @return ResponseEntity containing a paginated list of aquariums
     */
    @GetMapping
    @Operation(summary = "Get all aquariums", description = "Retrieve paginated list of aquariums")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTOAquariumList.class)))
    public ResponseEntity<ApiResponseDTO<List<AquariumResponseDTO>>> getAllAquariums(
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<Aquarium> page = aquariumService.getAllAquariums(pageable);
        List<AquariumResponseDTO> aquariums = page.getContent()
                .stream()
                .map(AquariumResponseDTO::fromEntity)
                .toList();

        Map<String, Object> paginationMeta = Map.of(
                "page", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages()
        );

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Aquariums retrieved successfully", aquariums, paginationMeta));
    }

    /**
     * Retrieves an aquarium by its ID.
     *
     * @param id ID of the aquarium to retrieve
     * @return ResponseEntity containing aquarium details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get aquarium by ID", description = "Retrieve details of a specific aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> getAquariumById(@PathVariable Long id) {
        Aquarium aquarium = aquariumService.getAquariumById(id);

        return ResponseEntity.ok(ApiResponseDTO.success("Aquarium retrieved successfully",
                AquariumResponseDTO.fromEntity(aquarium)));
    }

    /**
     * Creates a new aquarium.
     *
     * @param dto Aquarium details to be created
     * @return ResponseEntity containing created aquarium details
     */
    @PostMapping
    @Operation(summary = "Create a new aquarium", description = "Receive and save a new aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> createAquarium(
            @Valid @RequestBody CreateAquariumDTO dto) {
        Aquarium savedAquarium = aquariumService.createAquarium(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Aquarium created successfully",
                        AquariumResponseDTO.fromEntity(savedAquarium)));
    }

    /**
     * Updates an existing aquarium.
     *
     * @param id  ID of the aquarium to update
     * @param dto Updated aquarium details
     * @return ResponseEntity containing updated aquarium details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing aquarium", description = "Modify details of a specific aquarium")
    public ResponseEntity<ApiResponseDTO<AquariumResponseDTO>> updateAquarium(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAquariumDTO dto) {
        Aquarium updatedAquarium = aquariumService.updateAquarium(id, dto);

        return ResponseEntity.ok(ApiResponseDTO.success("Aquarium updated successfully",
                AquariumResponseDTO.fromEntity(updatedAquarium)));
    }

    /**
     * Deletes an aquarium by its ID.
     *
     * @param id ID of the aquarium to delete
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an aquarium", description = "Remove a specific aquarium")
    public ResponseEntity<Void> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);
        return ResponseEntity.noContent().build();
    }
}
