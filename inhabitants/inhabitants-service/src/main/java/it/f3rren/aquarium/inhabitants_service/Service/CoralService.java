package it.f3rren.aquarium.inhabitants_service.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import it.f3rren.aquarium.inhabitants_service.dto.CoralDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;

@Service
public class CoralService {
    
    private final RestTemplate restTemplate;
    
    @Value("${species.service.url:http://species-service:8083/species}")
    private String speciesServiceUrl;
    
    public CoralService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public CoralDTO getCoralById(Long id) {
        try {
            String url = speciesServiceUrl + "/corals/" + id;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                CoralDTO coral = new CoralDTO();
                coral.setId(((Number) data.get("id")).longValue());
                coral.setCommonName((String) data.get("commonName"));
                coral.setScientificName((String) data.get("scientificName"));
                coral.setType((String) data.get("type"));
                coral.setMinTankSize(data.get("minTankSize") != null ? ((Number) data.get("minTankSize")).intValue() : 0);
                coral.setMaxSize(data.get("maxSize") != null ? ((Number) data.get("maxSize")).intValue() : 0);
                coral.setDifficulty((String) data.get("difficulty"));
                coral.setLightRequirement((String) data.get("lightRequirement"));
                coral.setFlowRequirement((String) data.get("flowRequirement"));
                coral.setPlacement((String) data.get("placement"));
                coral.setAggressive(data.get("aggressive") != null ? (Boolean) data.get("aggressive") : false);
                coral.setFeeding((String) data.get("feeding"));
                coral.setDescription((String) data.get("description"));
                return coral;
            }
            throw new ResourceNotFoundException("Coral not found with id: " + id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Coral not found with id: " + id);
        }
    }
}
