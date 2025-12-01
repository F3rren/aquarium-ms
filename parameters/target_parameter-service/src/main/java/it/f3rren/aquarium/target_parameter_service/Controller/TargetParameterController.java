package it.f3rren.aquarium.target_parameter_service.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.target_parameter_service.Model.TargetParameter;
import it.f3rren.aquarium.target_parameter_service.Service.TargetParameterService;

@RestController
@RequestMapping("/api/target-parameters")
public class TargetParameterController {
    
    @Autowired
    private TargetParameterService targetParameterService;

    @GetMapping("/aquarium/{aquariumId}")
    public ResponseEntity<?> getTargetParameters(@PathVariable Long aquariumId) {
        TargetParameter targets = targetParameterService.getTargetParameters(aquariumId);
        
        if (targets == null) {
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Nessun parametro target personalizzato trovato",
                "data", (Object) null
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parametri target recuperati con successo",
            "data", targets
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/aquarium/{aquariumId}")
    public ResponseEntity<?> saveTargetParameters(
            @PathVariable Long aquariumId,
            @RequestBody TargetParameter targetParameter) {
        
        TargetParameter saved = targetParameterService.saveTargetParameters(aquariumId, targetParameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parametri target salvati con successo",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
