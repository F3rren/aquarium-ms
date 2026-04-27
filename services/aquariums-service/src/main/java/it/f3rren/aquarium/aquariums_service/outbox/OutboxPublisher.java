package it.f3rren.aquarium.aquariums_service.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Polling publisher per il Transactional Outbox Pattern.
 * Ogni 5 secondi legge gli eventi non ancora pubblicati e li invia a Kafka.
 * Se Kafka è temporaneamente down, gli eventi rimangono in outbox e verranno
 * ritentati al prossimo tick — zero message loss.
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OutboxPublisher(OutboxRepository outboxRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxRepository.findByPublishedAtIsNullOrderByCreatedAtAsc();
        if (pending.isEmpty()) return;

        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.executeInTransaction(ops ->
                        ops.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                );
                event.setPublishedAt(Instant.now());
                outboxRepository.save(event);
                log.info("Published outbox event {} [{}] to topic {}", event.getEventType(), event.getId(), event.getTopic());
            } catch (Exception ex) {
                log.error("Failed to publish outbox event {} — will retry", event.getId(), ex);
            }
        }
    }
}
