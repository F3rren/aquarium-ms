package it.f3rren.aquarium.inhabitants_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import it.f3rren.aquarium.inhabitants_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.inhabitants_service.dto.CoralDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;

@Service
public class CoralService {

    private static final Logger log = LoggerFactory.getLogger(CoralService.class);

    private final RestTemplate restTemplate;

    @Value("${species.service.url:http://species-service:8083/species}")
    private String speciesServiceUrl;

    public CoralService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CoralDTO getCoralById(Long id) {
        String url = speciesServiceUrl + "/corals/" + id;
        try {
            ResponseEntity<ApiResponseDTO<CoralDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponseDTO<CoralDTO>>() {}
            );

            ApiResponseDTO<CoralDTO> body = response.getBody();
            if (body != null && body.getSuccess() && body.getData() != null) {
                return body.getData();
            }

            throw new ResourceNotFoundException("Coral not found with id: " + id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Coral not found with id: " + id);
        } catch (ResourceAccessException e) {
            log.error("Species service unreachable when fetching coral {}: {}", id, e.getMessage());
            throw new RuntimeException("Species service is currently unavailable", e);
        }
    }
}
