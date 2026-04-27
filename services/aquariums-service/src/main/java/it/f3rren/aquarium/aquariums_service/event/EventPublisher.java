package it.f3rren.aquarium.aquariums_service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.f3rren.aquarium.aquariums_service.outbox.OutboxEvent;
import it.f3rren.aquarium.aquariums_service.outbox.OutboxRepository;
import it.f3rren.aquarium.events.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Scrive l'evento nella tabella outbox_events nella transazione corrente.
 * Non pubblica direttamente su Kafka: ci pensa OutboxPublisher in modo asincrono.
 */
@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public EventPublisher(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public void publish(BaseEvent event, String topic) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outbox = new OutboxEvent(event.getAggregateId(), event.getEventType(), payload, topic);
            outboxRepository.save(outbox);
            log.debug("Queued event {} for aggregate {} on topic {}", event.getEventType(), event.getAggregateId(), topic);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event " + event.getEventType(), e);
        }
    }
}
