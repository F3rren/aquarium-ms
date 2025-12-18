package it.f3rren.aquarium.ai.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String response;
    private String conversationId;
    private LocalDateTime timestamp;
    private String context;  // Informazioni sui dati utilizzati per la risposta
}
