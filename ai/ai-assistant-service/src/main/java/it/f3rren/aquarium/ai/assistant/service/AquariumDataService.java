package it.f3rren.aquarium.ai.assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Slf4j
@Service
public class AquariumDataService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${services.aquariums.url}")
    private String aquariumsUrl;

    public AquariumDataService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAllDataAsContext() {
        StringBuilder context = new StringBuilder();
        
        try {
            // Recupera lista acquari
            String aquariumsJson = webClient.get()
                .uri(aquariumsUrl + "/aquariums")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Errore nel recupero degli acquari: {}", e.getMessage());
                    return Mono.just("[]");
                })
                .block();
            
            context.append("=== ACQUARI ===\n");
            context.append(aquariumsJson != null ? aquariumsJson : "[]").append("\n\n");
            
            // Parse gli acquari per ottenere gli ID
            if (aquariumsJson != null && !aquariumsJson.equals("[]")) {
                try {
                    JsonNode aquariums = objectMapper.readTree(aquariumsJson);
                    
                    // Per ogni acquario, recupera abitanti, parametri e manutenzioni
                    for (JsonNode aquarium : aquariums) {
                        if (aquarium.has("id")) {
                            Long aquariumId = aquarium.get("id").asLong();
                            String aquariumName = aquarium.has("name") ? aquarium.get("name").asText() : "ID " + aquariumId;
                            
                            // Abitanti
                            String inhabitants = webClient.get()
                                .uri(aquariumsUrl + "/aquariums/" + aquariumId + "/inhabitants")
                                .retrieve()
                                .bodyToMono(String.class)
                                .onErrorResume(e -> Mono.just("[]"))
                                .block();
                            
                            if (inhabitants != null && !inhabitants.equals("[]")) {
                                context.append("=== ABITANTI ACQUARIO: ").append(aquariumName).append(" ===\n");
                                context.append(inhabitants).append("\n\n");
                            }
                            
                            // Parametri
                            String parameters = webClient.get()
                                .uri(aquariumsUrl + "/aquariums/" + aquariumId + "/parameters")
                                .retrieve()
                                .bodyToMono(String.class)
                                .onErrorResume(e -> Mono.just("[]"))
                                .block();
                            
                            if (parameters != null && !parameters.equals("[]")) {
                                context.append("=== PARAMETRI ACQUARIO: ").append(aquariumName).append(" ===\n");
                                context.append(parameters).append("\n\n");
                            }
                            
                            // Manutenzioni
                            String maintenance = webClient.get()
                                .uri(aquariumsUrl + "/aquariums/" + aquariumId + "/tasks")
                                .retrieve()
                                .bodyToMono(String.class)
                                .onErrorResume(e -> Mono.just("[]"))
                                .block();
                            
                            if (maintenance != null && !maintenance.equals("[]")) {
                                context.append("=== MANUTENZIONI ACQUARIO: ").append(aquariumName).append(" ===\n");
                                context.append(maintenance).append("\n\n");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Errore nel parsing degli acquari", e);
                }
            }
            
        } catch (Exception e) {
            log.error("Errore nel recupero dei dati", e);
        }
        
        return context.toString();
    }
}

