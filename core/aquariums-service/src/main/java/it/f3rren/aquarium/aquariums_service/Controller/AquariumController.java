package it.f3rren.aquarium.aquariums_service.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.aquariums_service.Client.ParametersClient;
import it.f3rren.aquarium.aquariums_service.DTO.ManualParameterDTO;
import it.f3rren.aquarium.aquariums_service.DTO.TargetParameterDTO;
import it.f3rren.aquarium.aquariums_service.DTO.WaterParameterDTO;
import it.f3rren.aquarium.aquariums_service.Model.Aquarium;
import it.f3rren.aquarium.aquariums_service.Service.AquariumService;

@RestController
@RequestMapping("api/aquariums")
public class AquariumController {

    @Autowired
    private AquariumService aquariumService;

    @Autowired
    private ParametersClient parametersClient;

    // ========== AQUARIUM ENDPOINTS ==========

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
        Aquarium savedAquarium = aquariumService.createAquarium(aquarium);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario creato con successo",
                "data", savedAquarium);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAquarium(@PathVariable Long id, @RequestBody Aquarium aquarium) {
        Aquarium updatedAquarium = aquariumService.updateAquarium(id, aquarium);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario modificato con successo",
                "data", updatedAquarium);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario eliminato con successo");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ========== WATER PARAMETERS ENDPOINTS ==========

    @PostMapping("/{id}/parameters")
    public ResponseEntity<?> addWaterParameter(@PathVariable Long id, @RequestBody WaterParameterDTO parameter) {
        parameter.setAquariumId(id);
        return ResponseEntity.ok(parametersClient.addWaterParameter(parameter));
    }

    @GetMapping("/{id}/parameters")
    public ResponseEntity<?> getWaterParameters(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(parametersClient.getWaterParametersByAquarium(id, limit));
    }

    @GetMapping("/{id}/parameters/latest")
    public ResponseEntity<?> getLatestWaterParameter(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getLatestWaterParameter(id));
    }

    @GetMapping("/{id}/parameters/history")
    public ResponseEntity<?> getWaterParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(parametersClient.getWaterParametersHistory(id, period, from, to));
    }

    // ========== MANUAL PARAMETERS ENDPOINTS ==========

    @PostMapping("/{id}/parameters/manual")
    public ResponseEntity<?> addManualParameter(@PathVariable Long id, @RequestBody ManualParameterDTO parameter) {
        parameter.setAquariumId(id);
        return ResponseEntity.ok(parametersClient.addManualParameter(parameter));
    }

    @GetMapping("/{id}/parameters/manual")
    public ResponseEntity<?> getLatestManualParameter(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getLatestManualParameter(id));
    }

    @GetMapping("/{id}/parameters/manual/history")
    public ResponseEntity<?> getManualParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(parametersClient.getManualParametersHistory(id, from, to));
    }

    // ========== TARGET PARAMETERS ENDPOINTS ==========

    @GetMapping("/{id}/settings/targets")
    public ResponseEntity<?> getTargetParameters(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getTargetParameters(id));
    }

    @PostMapping("/{id}/settings/targets")
    public ResponseEntity<?> saveTargetParameters(
            @PathVariable Long id,
            @RequestBody TargetParameterDTO targetParameter) {
        return ResponseEntity.ok(parametersClient.saveTargetParameters(id, targetParameter));
    }

}
