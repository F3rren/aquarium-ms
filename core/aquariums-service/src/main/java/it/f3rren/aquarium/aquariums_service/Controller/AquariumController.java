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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/aquariums")
@Tag(name = "Aquarium", description = "API for managing aquariums")
public class AquariumController {

    @Autowired
    private AquariumService aquariumService;

    @Autowired
    private ParametersClient parametersClient;

    @GetMapping
    @Operation(summary = "Get all aquariums", description = "Retrieve details of all aquariums")
    public ResponseEntity<?> getAllAquariums() {
        List<Aquarium> aquariums = aquariumService.getAllAquariums();

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquari recuperati con successo",
                "data", aquariums);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get aquarium by ID", description = "Retrieve details of a specific aquarium")
    public ResponseEntity<?> getAquariumById(@PathVariable Long id) {
        Aquarium aquarium = aquariumService.getAquariumById(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario recuperato con successo",
                "data", aquarium);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    @PostMapping
    @Operation(summary = "Create a new aquarium", description = "Receive and save a new aquarium")
    public ResponseEntity<?> createAquarium(@RequestBody Aquarium aquarium) {
        Aquarium savedAquarium = aquariumService.createAquarium(aquarium);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario creato con successo",
                "data", savedAquarium);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing aquarium", description = "Modify details of a specific aquarium")
    public ResponseEntity<?> updateAquarium(@PathVariable Long id, @RequestBody Aquarium aquarium) {
        Aquarium updatedAquarium = aquariumService.updateAquarium(id, aquarium);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario modificato con successo",
                "data", updatedAquarium);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an aquarium", description = "Remove a specific aquarium")
    public ResponseEntity<?> deleteAquarium(@PathVariable Long id) {
        aquariumService.deleteAquarium(id);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "Acquario eliminato con successo");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/parameters")
    @Operation(summary = "Add a new water parameter", description = "Add a new water parameter to a specific aquarium")
    public ResponseEntity<?> addWaterParameter(@PathVariable Long id, @RequestBody WaterParameterDTO parameter) {
        parameter.setAquariumId(id);
        return ResponseEntity.ok(parametersClient.addWaterParameter(parameter));
    }

    @GetMapping("/{id}/parameters")
    @Operation(summary = "Get water parameters", description = "Retrieve water parameters for a specific aquarium")
    public ResponseEntity<?> getWaterParameters(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(parametersClient.getWaterParametersByAquarium(id, limit));
    }

    @GetMapping("/{id}/parameters/latest")
    @Operation(summary = "Get latest water parameter", description = "Retrieve the latest water parameter for a specific aquarium")
    public ResponseEntity<?> getLatestWaterParameter(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getLatestWaterParameter(id));
    }

    @GetMapping("/{id}/parameters/history")
    @Operation(summary = "Get water parameters history", description = "Retrieve historical water parameters for a specific aquarium")
    public ResponseEntity<?> getWaterParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(parametersClient.getWaterParametersHistory(id, period, from, to));
    }

    @PostMapping("/{id}/parameters/manual")
    @Operation(summary = "Add a new manual parameter", description = "Add a new manual parameter to a specific aquarium")
    public ResponseEntity<?> addManualParameter(@PathVariable Long id, @RequestBody ManualParameterDTO parameter) {
        parameter.setAquariumId(id);
        return ResponseEntity.ok(parametersClient.addManualParameter(parameter));
    }

    @GetMapping("/{id}/parameters/manual")
    @Operation(summary = "Get latest manual parameter", description = "Retrieve the latest manual parameter for a specific aquarium")
    public ResponseEntity<?> getLatestManualParameter(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getLatestManualParameter(id));
    }

    @GetMapping("/{id}/parameters/manual/history")
    @Operation(summary = "Get manual parameters history", description = "Retrieve historical manual parameters for a specific aquarium")
    public ResponseEntity<?> getManualParametersHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(parametersClient.getManualParametersHistory(id, from, to));
    }

    @GetMapping("/{id}/settings/targets")
    @Operation(summary = "Get target parameters", description = "Retrieve target parameters for a specific aquarium")
    public ResponseEntity<?> getTargetParameters(@PathVariable Long id) {
        return ResponseEntity.ok(parametersClient.getTargetParameters(id));
    }

    @PostMapping("/{id}/settings/targets")
    @Operation(summary = "Save target parameters", description = "Save target parameters for a specific aquarium")
    public ResponseEntity<?> saveTargetParameters(
            @PathVariable Long id,
            @RequestBody TargetParameterDTO targetParameter) {
        return ResponseEntity.ok(parametersClient.saveTargetParameters(id, targetParameter));
    }

}
