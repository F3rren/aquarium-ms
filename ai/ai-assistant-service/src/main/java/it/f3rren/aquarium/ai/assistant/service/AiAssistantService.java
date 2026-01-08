package it.f3rren.aquarium.ai.assistant.service;

import it.f3rren.aquarium.ai.assistant.dto.ChatRequest;
import it.f3rren.aquarium.ai.assistant.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistantService {

    private final ChatModel chatModel;
    private final AquariumDataService aquariumDataService;
    
    // Mappa per memorizzare le conversazioni (in produzione usare Redis o database)
    private final ConcurrentHashMap<String, List<Message>> conversations = new ConcurrentHashMap<>();

    public ChatResponse chat(ChatRequest request) {
        try {
            String conversationId = request.getConversationId() != null 
                ? request.getConversationId() 
                : UUID.randomUUID().toString();

            // Recupera o crea la conversazione
            List<Message> conversationHistory = conversations.computeIfAbsent(
                conversationId, 
                k -> new ArrayList<>()
            );

            // Se è la prima interazione, aggiungi il messaggio di sistema con i dati
            if (conversationHistory.isEmpty()) {
                String aquariumData = aquariumDataService.getAllDataAsContext();
                String systemPrompt = buildSystemPrompt(aquariumData);
                conversationHistory.add(new SystemMessage(systemPrompt));
            }

            // Aggiungi il messaggio dell'utente
            conversationHistory.add(new UserMessage(request.getMessage()));

            // Genera la risposta
            Prompt prompt = new Prompt(conversationHistory);
            String aiResponse = chatModel.call(prompt).getResult().getOutput().getContent();

            // Salva la risposta nella conversazione
            conversationHistory.add(new SystemMessage(aiResponse));

            // Limita la dimensione della conversazione (ultimi 10 messaggi)
            if (conversationHistory.size() > 10) {
                conversationHistory.subList(0, conversationHistory.size() - 10).clear();
            }

            return ChatResponse.builder()
                .response(aiResponse)
                .conversationId(conversationId)
                .timestamp(LocalDateTime.now())
                .context("Dati analizzati: acquari, abitanti, parametri e manutenzioni")
                .build();

        } catch (Exception e) {
            log.error("Errore durante la generazione della risposta AI", e);
            return ChatResponse.builder()
                .response("Mi dispiace, si è verificato un errore. Riprova più tardi.")
                .conversationId(request.getConversationId())
                .timestamp(LocalDateTime.now())
                .build();
        }
    }

    private String buildSystemPrompt(String aquariumData) {
        return """
            Sei un assistente AI esperto in acquariologia. Rispondi in italiano in modo conciso e chiaro.
            
            DATI ACQUARI:
            %s
            
            Usa questi dati per rispondere. Sii breve e diretto.
            """.formatted(aquariumData);
    }

    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
        log.info("Conversazione {} eliminata", conversationId);
    }
}
