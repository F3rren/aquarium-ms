package it.f3rren.aquarium.manual_parameters_service.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.manual_parameters_service.Model.ManualParameter;
import it.f3rren.aquarium.manual_parameters_service.Service.ManualParameterService;

@RestController
@RequestMapping("/api/manual-parameters")
public class ManualParameterController {
    
    @Autowired
    private ManualParameterService manualParameterService;

    @PostMapping
    public ResponseEntity<?> addManualParameter(@RequestBody ManualParameter parameter) {
        ManualParameter saved = manualParameterService.saveManualParameter(parameter.getAquariumId(), parameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parametro manuale salvato con successo",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/aquarium/{aquariumId}/latest")
    public ResponseEntity<?> getLatestManualParameter(@PathVariable Long aquariumId) {
        ManualParameter latest = manualParameterService.getLatestManualParameter(aquariumId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Ultimo parametro manuale recuperato con successo",
            "data", latest
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/aquarium/{aquariumId}")
    public ResponseEntity<?> getAllManualParameters(@PathVariable Long aquariumId) {
        List<ManualParameter> parameters = manualParameterService.getAllManualParameters(aquariumId);

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parametri manuali recuperati con successo",
            "data", parameters,
            "metadata", Map.of(
                "aquariumId", aquariumId,
                "count", parameters.size()
            )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/aquarium/{aquariumId}/history")
    public ResponseEntity<?> getManualParametersHistory(
            @PathVariable Long aquariumId,
            @RequestParam String from,
            @RequestParam String to) {
        
        LocalDateTime fromDate = LocalDateTime.parse(from);
        LocalDateTime toDate = LocalDateTime.parse(to);
        
        List<ManualParameter> parameters = manualParameterService.getManualParametersHistory(
            aquariumId, fromDate, toDate
        );

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Storico parametri manuali recuperato con successo",
            "data", parameters
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
