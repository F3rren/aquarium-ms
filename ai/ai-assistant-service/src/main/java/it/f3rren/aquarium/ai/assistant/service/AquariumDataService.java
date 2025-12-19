package it.f3rren.aquarium.ai.assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


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

    @Cacheable(value = "aquariumData", unless = "#result == null")
    public String getAllDataAsContext() {
        log.info("Caricamento dati acquario (questo avviene solo se non in cache)");
        StringBuilder context = new StringBuilder();
        
        try {
            // Recupera lista acquari con timeout
            String aquariumsJson = webClient.get()
                .uri(aquariumsUrl + "/aquariums")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
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
                    
                    // Per ogni acquario, recupera abitanti, parametri e manutenzioni IN PARALLELO
                    List<Mono<String>> aquariumDataMonos = new ArrayList<>();
                    
                    for (JsonNode aquarium : aquariums) {
                        if (aquarium.has("id")) {
                            Long aquariumId = aquarium.get("id").asLong();
                            String aquariumName = aquarium.has("name") ? aquarium.get("name").asText() : "ID " + aquariumId;
                            
                            // Crea Mono per recuperare tutti i dati di questo acquario in parallelo
                            Mono<String> aquariumDataMono = Mono.zip(
                                // Abitanti
                                webClient.get()
                                    .uri(aquariumsUrl + "/aquariums/" + aquariumId + "/inhabitants")
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .timeout(Duration.ofSeconds(5))
                                    .onErrorResume(e -> Mono.just("[]")),
                                // Parametri
                                webClient.get()
                                    .uri(aquariumsUrl + "/aquariums/" + aquariumId + "/parameters")
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .timeout(Duration.ofSeconds(5))
                                    .onErrorResume(e -> Mono.just("[]")),
                                // Manutenzioni
                                webClient.get()
                                    .uri(aquariumsUrl + "/aquariums/" + aquariumId + "/tasks")
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .timeout(Duration.ofSeconds(5))
                                    .onErrorResume(e -> Mono.just("{}"))
                            ).map(tuple -> {
                                StringBuilder sb = new StringBuilder();
                                String inhabitants = tuple.getT1();
                                String parameters = tuple.getT2();
                                String maintenance = tuple.getT3();
                                
                                if (inhabitants != null && !inhabitants.equals("[]")) {
                                    sb.append("=== ABITANTI ACQUARIO: ").append(aquariumName).append(" ===\n");
                                    sb.append(inhabitants).append("\n\n");
                                }
                                
                                if (parameters != null && !parameters.equals("[]")) {
                                    sb.append("=== PARAMETRI ACQUARIO: ").append(aquariumName).append(" ===\n");
                                    sb.append(parameters).append("\n\n");
                                }
                                
                                if (maintenance != null && !maintenance.equals("{}") && !maintenance.equals("[]")) {
                                    sb.append("=== MANUTENZIONI ACQUARIO: ").append(aquariumName).append(" ===\n");
                                    sb.append(maintenance).append("\n\n");
                                }
                                
                                return sb.toString();
                            });
                            
                            aquariumDataMonos.add(aquariumDataMono);
                        }
                    }
                    
                    // Esegui tutte le chiamate in parallelo e attendi il completamento
                    if (!aquariumDataMonos.isEmpty()) {
                        List<String> results = Flux.merge(aquariumDataMonos)
                            .collectList()
                            .block(Duration.ofSeconds(30)); // Timeout totale di 30 secondi
                        
                        if (results != null) {
                            results.forEach(context::append);
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
    
    @CacheEvict(value = "aquariumData", allEntries = true)
    public void clearCache() {
        log.info("Cache dei dati acquario svuotata");
    }
}

