package it.f3rren.aquarium.aquariums_service.outbox;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Transactional Outbox: persiste l'evento nel DB nella stessa transazione
 * della business operation. Uno scheduler separato lo legge e lo pubblica su Kafka,
 * garantendo che nessun evento vada perso anche in caso di crash del servizio.
 */
@Entity
@Table(name = "outbox_events", schema = "core")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    public OutboxEvent() {}

    public OutboxEvent(String aggregateId, String eventType, String payload, String topic) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.topic = topic;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public String getTopic() { return topic; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
}
