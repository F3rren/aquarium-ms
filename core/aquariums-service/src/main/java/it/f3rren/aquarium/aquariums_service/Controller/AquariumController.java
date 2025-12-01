package it.f3rren.aquarium.aquariums_service.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import it.f3rren.aquarium.aquariums_service.Model.Aquarium;
import it.f3rren.aquarium.aquariums_service.Service.AquariumService;

@RestController
@RequestMapping("api/aquariums")
public class AquariumController {

    @Autowired
    private AquariumService aquariumService;

    @GetMapping
    public ResponseEntity<?> getAllAquariums() {
        List<Aquarium> aquariums = aquariumService.getAllAquariums();

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquari recuperati con successo",
                "data", aquariums);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAquariumById(@PathVariable Long id) {
        Aquarium aquarium = aquariumService.getAquariumById(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario recuperato con successo",
                "data", aquarium);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createAquarium(@RequestBody Aquarium aquarium) {
        Aquarium created = aquariumService.createAquarium(aquarium);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Acquario creato con successo",
            "data", created
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAquarium(@PathVariable Long id, @RequestBody Aquarium aquarium) {
        Aquarium updated = aquariumService.updateAquarium(id, aquarium);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Acquario aggiornato con successo",
            "data", updated
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Acquario eliminato con successo"
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
