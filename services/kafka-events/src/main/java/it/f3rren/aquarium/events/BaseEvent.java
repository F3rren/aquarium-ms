package it.f3rren.aquarium.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events published to Kafka.
 * The eventId guarantees idempotent processing: consumers can skip
 * duplicates by checking if the eventId was already processed.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AquariumCreatedEvent.class, name = "AquariumCreated"),
    @JsonSubTypes.Type(value = AquariumDeletedEvent.class, name = "AquariumDeleted"),
    @JsonSubTypes.Type(value = ParameterMeasuredEvent.class, name = "ParameterMeasured"),
    @JsonSubTypes.Type(value = MaintenanceTaskCompletedEvent.class, name = "MaintenanceTaskCompleted")
})
public abstract class BaseEvent {

    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;

    protected BaseEvent(String eventType, String aggregateId) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.occurredAt = Instant.now();
    }

    protected BaseEvent() {
        this.eventId = null;
        this.eventType = null;
        this.aggregateId = null;
        this.occurredAt = null;
    }

    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getAggregateId() { return aggregateId; }
}
