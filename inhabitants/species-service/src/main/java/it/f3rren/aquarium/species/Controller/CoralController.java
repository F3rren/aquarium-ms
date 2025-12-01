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

import it.f3rren.aquarium.species.Model.Coral;
import it.f3rren.aquarium.species.Service.CoralService;

@RestController
@RequestMapping("api/species/corals")
public class CoralController {
 
    @Autowired
    public CoralService coralService;

    @GetMapping
    public ResponseEntity<?> getAllCorals() {
        List<Coral> corals = coralService.getAllCorals();
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Coralli recuperati con successo",
            "data", corals
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCoralById(@PathVariable Long id) {
        Coral coral = coralService.getCoralById(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Corallo recuperato con successo",
            "data", coral
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
