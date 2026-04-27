package it.f3rren.aquarium.inhabitants_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

/**
 * Tiene traccia degli eventId già processati per garantire idempotenza:
 * se Kafka consegna lo stesso messaggio due volte (at-least-once), il secondo
 * viene scartato prima di toccare il database.
 */
@Repository
public class ProcessedEventRepository {

    private final JdbcTemplate jdbc;

    public ProcessedEventRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsByEventId(String eventId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM inhabitants.processed_events WHERE event_id = ?",
                Integer.class, eventId);
        return count != null && count > 0;
    }

    public void markProcessed(String eventId, Instant processedAt) {
        jdbc.update(
                "INSERT INTO inhabitants.processed_events (event_id, processed_at) VALUES (?, ?)",
                eventId, java.sql.Timestamp.from(processedAt));
    }
}
