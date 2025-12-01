package it.f3rren.aquarium.water_parameters_service.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.water_parameters_service.Model.Parameter;
import it.f3rren.aquarium.water_parameters_service.Service.ParameterService;

@RestController
@RequestMapping("/api/water-parameters")
public class WaterParameterController {
    
    @Autowired
    private ParameterService parameterService;

    @PostMapping
    public ResponseEntity<?> addParameter(@RequestBody Parameter parameter) {
        Parameter saved = parameterService.saveParameter(parameter.getAquariumId(), parameter);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parametro salvato con successo",
            "data", saved
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/aquarium/{aquariumId}")
    public ResponseEntity<?> getParametersByAquarium(
            @PathVariable Long aquariumId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<Parameter> parameters = parameterService.getParametersByAquariumId(aquariumId, limit);

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Parametri recuperati con successo",
            "data", parameters,
            "metadata", Map.of(
                "aquariumId", aquariumId,
                "count", parameters.size(),
                "limit", limit
            )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/aquarium/{aquariumId}/latest")
    public ResponseEntity<?> getLatestParameter(@PathVariable Long aquariumId) {
        Parameter latest = parameterService.getLatestParameter(aquariumId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Ultimo parametro recuperato con successo",
            "data", latest
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/aquarium/{aquariumId}/history")
    public ResponseEntity<?> getParametersHistory(
            @PathVariable Long aquariumId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        
        List<Parameter> parameters;
        
        if (period != null) {
            parameters = parameterService.getParametersByPeriod(aquariumId, period);
        } else if (from != null && to != null) {
            LocalDateTime fromDate = LocalDateTime.parse(from);
            LocalDateTime toDate = LocalDateTime.parse(to);
            parameters = parameterService.getParametersHistory(aquariumId, fromDate, toDate);
        } else {
            parameters = parameterService.getParametersByPeriod(aquariumId, "week");
        }

        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Storico parametri recuperato con successo",
            "data", parameters
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
