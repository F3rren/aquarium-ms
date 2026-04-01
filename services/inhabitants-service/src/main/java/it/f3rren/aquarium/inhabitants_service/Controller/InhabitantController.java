package it.f3rren.aquarium.inhabitants_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import it.f3rren.aquarium.inhabitants_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.model.Inhabitant;
import it.f3rren.aquarium.inhabitants_service.service.IInhabitantService;

@RestController
@RequestMapping("/aquariums")
@Tag(name = "Inhabitant", description = "API for managing inhabitants")
public class InhabitantController {

    private final IInhabitantService inhabitantService;

    public InhabitantController(IInhabitantService inhabitantService) {
        this.inhabitantService = inhabitantService;
    }

    @GetMapping("/{id}/inhabitants")
    @Operation(summary = "Get inhabitants by aquarium ID", description = "Retrieve details of inhabitants in a specific aquarium")
    public ResponseEntity<ApiResponseDTO<List<InhabitantDetailsDTO>>> getAquariumInhabitants(@PathVariable Long id) {
        List<InhabitantDetailsDTO> inhabitants = inhabitantService.getInhabitantsByAquariumId(id);

        ApiResponseDTO<List<InhabitantDetailsDTO>> response = new ApiResponseDTO<>(
                true,
                "Inhabitants retrieved successfully",
                inhabitants,
                Map.of(
                        "aquariumId", id,
                        "totalCount", inhabitants.size(),
                        "fishCount", inhabitants.stream().filter(i -> "fish".equals(i.getType())).count(),
                        "coralCount", inhabitants.stream().filter(i -> "coral".equals(i.getType())).count()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/inhabitants")
    @Operation(summary = "Add an inhabitant to an aquarium", description = "Add a new inhabitant to a specific aquarium")
    public ResponseEntity<ApiResponseDTO<Inhabitant>> addInhabitantToAquarium(
            @PathVariable Long id,
            @Valid @RequestBody CreateInhabitantDTO dto) {

        Inhabitant saved = inhabitantService.addInhabitant(id, dto);

        ApiResponseDTO<Inhabitant> response = new ApiResponseDTO<>(
                true,
                "Inhabitant added successfully",
                saved,
                null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{aquariumId}/inhabitants/{inhabitantId}")
    @Operation(summary = "Update an inhabitant", description = "Update quantity, notes, custom name, current size or other custom fields of an inhabitant")
    public ResponseEntity<ApiResponseDTO<Inhabitant>> updateInhabitant(
            @PathVariable Long aquariumId,
            @PathVariable Long inhabitantId,
            @Valid @RequestBody UpdateInhabitantDTO dto) {

        Inhabitant updated = inhabitantService.updateInhabitant(aquariumId, inhabitantId, dto);

        ApiResponseDTO<Inhabitant> response = new ApiResponseDTO<>(
                true,
                "Inhabitant updated successfully",
                updated,
                null);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{aquariumId}/inhabitants/{inhabitantId}")
    @Operation(summary = "Remove an inhabitant from an aquarium", description = "Remove an inhabitant from a specific aquarium")
    public ResponseEntity<ApiResponseDTO<Void>> removeInhabitantFromAquarium(
            @PathVariable Long aquariumId,
            @PathVariable Long inhabitantId) {

        inhabitantService.removeInhabitant(aquariumId, inhabitantId);

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(
                true,
                "Inhabitant removed successfully",
                null,
                null);

        return ResponseEntity.ok(response);
    }
}
