package it.f3rren.aquarium.aquariums_service.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.aquariums_service.Model.Aquarium;
import it.f3rren.aquarium.aquariums_service.Service.AquariumService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/aquariums")
@Tag(name = "Aquarium", description = "API for managing aquariums")
public class AquariumController {

    @Autowired
    private AquariumService aquariumService;

    @GetMapping
    @Operation(summary = "Get all aquariums", description = "Retrieve details of all aquariums")
    public ResponseEntity<?> getAllAquariums() {
        List<Aquarium> aquariums = aquariumService.getAllAquariums();

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Aquariums retrieved successfully",
                "data", aquariums);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get aquarium by ID", description = "Retrieve details of a specific aquarium")
    public ResponseEntity<?> getAquariumById(@PathVariable Long id) {
        Aquarium aquarium = aquariumService.getAquariumById(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Aquarium retrieved successfully",
                "data", aquarium);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    @PostMapping
    @Operation(summary = "Create a new aquarium", description = "Receive and save a new aquarium")
    public ResponseEntity<?> createAquarium(@RequestBody Aquarium aquarium) {
        Aquarium savedAquarium = aquariumService.createAquarium(aquarium);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Aquarium created successfully",
                "data", savedAquarium);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing aquarium", description = "Modify details of a specific aquarium")
    public ResponseEntity<?> updateAquarium(@PathVariable Long id, @RequestBody Aquarium aquarium) {
        Aquarium updatedAquarium = aquariumService.updateAquarium(id, aquarium);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Aquarium updated successfully",
                "data", updatedAquarium);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an aquarium", description = "Remove a specific aquarium")
    public ResponseEntity<?> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Aquarium deleted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
