package it.f3rren.aquarium.inhabitants_service.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.inhabitants_service.DTO.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.Model.Inhabitant;
import it.f3rren.aquarium.inhabitants_service.Service.InhabitantService;

@RestController
@RequestMapping("/aquariums")
@Tag(name = "Inhabitant", description = "API for managing inhabitants")
public class InhabitantController {

    @Autowired
    private InhabitantService inhabitantService;

    @GetMapping("/{id}/inhabitants")
    @Operation(summary = "Get inhabitants by aquarium ID", description = "Retrieve details of inhabitants in a specific aquarium")
    public ResponseEntity<?> getAquariumInhabitants(@PathVariable Long id) {
        List<InhabitantDetailsDTO> inhabitants = inhabitantService.getInhabitantsByAquariumId(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Inhabitants retrieved successfully",
                "data", inhabitants,
                "metadata", Map.of(
                        "aquariumId", id,
                        "totalCount", inhabitants.size(),
                        "fishCount", inhabitants.stream().filter(i -> "fish".equals(i.getType())).count(),
                        "coralCount", inhabitants.stream().filter(i -> "coral".equals(i.getType())).count()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/inhabitants")
    @Operation(summary = "Add an inhabitant to an aquarium", description = "Add a new inhabitant to a specific aquarium")
    public ResponseEntity<?> addInhabitantToAquarium(
            @PathVariable Long id,
            @RequestBody Inhabitant inhabitant) {
        
        inhabitant.setAquariumId(id);
        Inhabitant saved = inhabitantService.addInhabitant(inhabitant);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Inhabitant added successfully",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{aquariumId}/inhabitants/{inhabitantId}")
    @Operation(summary = "Remove an inhabitant from an aquarium", description = "Remove an inhabitant from a specific aquarium")
    public ResponseEntity<?> removeInhabitantFromAquarium(
            @PathVariable Long aquariumId,
            @PathVariable Long inhabitantId) {
        
        inhabitantService.removeInhabitant(inhabitantId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Inhabitant removed successfully"
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }   
}
