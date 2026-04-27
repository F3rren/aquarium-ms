package it.f3rren.aquarium.maintenance_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class ProcessedEventRepository {

    private final JdbcTemplate jdbc;

    public ProcessedEventRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsByEventId(String eventId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM maintenance.processed_events WHERE event_id = ?",
                Integer.class, eventId);
        return count != null && count > 0;
    }

    public void markProcessed(String eventId, Instant processedAt) {
        jdbc.update(
                "INSERT INTO maintenance.processed_events (event_id, processed_at) VALUES (?, ?)",
                eventId, java.sql.Timestamp.from(processedAt));
    }
}
