package it.f3rren.aquarium.inhabitants_service.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import it.f3rren.aquarium.inhabitants_service.DTO.FishDTO;
import it.f3rren.aquarium.inhabitants_service.Exception.ResourceNotFoundException;

@Service
public class FishService {
    
    private final RestTemplate restTemplate;
    
    @Value("${species.service.url:http://species-service:8083/species}")
    private String speciesServiceUrl;
    
    public FishService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public FishDTO getFishById(Long id) {
        try {
            String url = speciesServiceUrl + "/fishs/" + id;
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                
                FishDTO fish = new FishDTO();
                fish.setId(((Number) data.get("id")).longValue());
                fish.setCommonName((String) data.get("commonName"));
                fish.setScientificName((String) data.get("scientificName"));
                fish.setFamily((String) data.get("family"));
                fish.setMinTankSize(data.get("minTankSize") != null ? ((Number) data.get("minTankSize")).intValue() : 0);
                fish.setMaxSize(data.get("maxSize") != null ? ((Number) data.get("maxSize")).intValue() : 0);
                fish.setDifficulty((String) data.get("difficulty"));
                fish.setReefSafe(data.get("reefSafe") != null ? (Boolean) data.get("reefSafe") : false);
                fish.setTemperament((String) data.get("temperament"));
                fish.setDiet((String) data.get("diet"));
                fish.setImageUrl((String) data.get("imageUrl"));
                fish.setDescription((String) data.get("description"));
                fish.setWaterType((String) data.get("waterType"));
                return fish;
            }
            throw new ResourceNotFoundException("Fish not found with id: " + id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Fish not found with id: " + id);
        }
    }
}
