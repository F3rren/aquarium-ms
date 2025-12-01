package it.f3rren.aquarium.inhabitants_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import it.f3rren.aquarium.inhabitants_service.DTO.CoralDTO;
import it.f3rren.aquarium.inhabitants_service.Exception.ResourceNotFoundException;

@Service
public class CoralService {
    
    private final RestTemplate restTemplate;
    
    @Value("${species.service.url}")
    private String speciesServiceUrl;
    
    public CoralService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public CoralDTO getCoralById(Long id) {
        try {
            String url = speciesServiceUrl + "/api/species/corals/" + id;
            return restTemplate.getForObject(url, CoralDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Corallo non trovato con id: " + id);
        }
    }
}
