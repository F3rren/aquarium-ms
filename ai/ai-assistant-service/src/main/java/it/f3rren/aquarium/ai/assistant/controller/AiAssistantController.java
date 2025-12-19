package it.f3rren.aquarium.ai.assistant.controller;

import it.f3rren.aquarium.ai.assistant.dto.ChatRequest;
import it.f3rren.aquarium.ai.assistant.dto.ChatResponse;
import it.f3rren.aquarium.ai.assistant.service.AiAssistantService;
import it.f3rren.aquarium.ai.assistant.service.AquariumDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;
    private final AquariumDataService aquariumDataService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Richiesta chat ricevuta: {}", request.getMessage());
        ChatResponse response = aiAssistantService.chat(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity<Void> clearConversation(@PathVariable String conversationId) {
        log.info("Eliminazione conversazione: {}", conversationId);
        aiAssistantService.clearConversation(conversationId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        log.info("Richiesta di pulizia della cache");
        aquariumDataService.clearCache();
        return ResponseEntity.ok("Cache svuotata con successo. I dati saranno ricaricati alla prossima richiesta.");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Assistant Service is running");
    }
}
