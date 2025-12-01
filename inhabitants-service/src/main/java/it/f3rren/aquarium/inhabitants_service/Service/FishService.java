package it.f3rren.aquarium.inhabitants_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import it.f3rren.aquarium.inhabitants_service.DTO.FishDTO;
import it.f3rren.aquarium.inhabitants_service.Exception.ResourceNotFoundException;

@Service
public class FishService {
    
    private final RestTemplate restTemplate;
    
    @Value("${species.service.url}")
    private String speciesServiceUrl;
    
    public FishService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public FishDTO getFishById(Long id) {
        try {
            String url = speciesServiceUrl + "/api/species/fishs/" + id;
            return restTemplate.getForObject(url, FishDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Pesce non trovato con id: " + id);
        }
    }
}
