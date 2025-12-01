package it.f3rren.aquarium.inhabitants_service.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.f3rren.aquarium.inhabitants_service.DTO.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.Model.Inhabitant;
import it.f3rren.aquarium.inhabitants_service.Service.InhabitantService;

@RestController
@RequestMapping("api/aquariums")
@CrossOrigin(origins = "*")

public class InhabitantController {

    @Autowired
    private InhabitantService inhabitantService;

    @GetMapping("/{id}/inhabitants")
    public ResponseEntity<?> getAquariumInhabitants(@PathVariable Long id) {
        List<InhabitantDetailsDTO> inhabitants = inhabitantService.getInhabitantsByAquariumId(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Abitanti recuperati con successo",
                "data", inhabitants,
                "metadata", Map.of(
                        "aquariumId", id,
                        "totalCount", inhabitants.size(),
                        "fishCount", inhabitants.stream().filter(i -> "fish".equals(i.getType())).count(),
                        "coralCount", inhabitants.stream().filter(i -> "coral".equals(i.getType())).count()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/inhabitants")
    public ResponseEntity<?> addInhabitantToAquarium(
            @PathVariable Long id,
            @RequestBody Inhabitant inhabitant) {
        
        inhabitant.setAquariumId(id);
        Inhabitant saved = inhabitantService.addInhabitant(inhabitant);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Abitante aggiunto con successo",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{aquariumId}/inhabitants/{inhabitantId}")
    public ResponseEntity<?> removeInhabitantFromAquarium(
            @PathVariable Long aquariumId,
            @PathVariable Long inhabitantId) {
        
        inhabitantService.removeInhabitant(inhabitantId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Abitante rimosso con successo"
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

   
}
