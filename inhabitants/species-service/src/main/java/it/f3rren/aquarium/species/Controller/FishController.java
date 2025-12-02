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
import it.f3rren.aquarium.species.Model.Fish;
import it.f3rren.aquarium.species.Service.FishService;

@RestController
@RequestMapping("api/species/fishs")
@Tag(name = "Fish", description = "API for managing fish")
public class FishController {
    
    @Autowired
    private FishService fishService;

    @GetMapping
    @Operation(summary = "Get all fishs", description = "Retrieve a list of all fishs")
    public ResponseEntity<?> getAllFishs() {
        List<Fish> fishs = fishService.getAllFishs();
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Pesci recuperati con successo",
            "data", fishs
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a fish by ID", description = "Retrieve details of a specific fish by its ID")
    public ResponseEntity<?> getFishById(@PathVariable Long id) {
        Fish fish = fishService.getFishById(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Pesce recuperato con successo",
            "data", fish
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
