package it.f3rren.aquarium.aquariums_service.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    List<OutboxEvent> findByPublishedAtIsNullOrderByCreatedAtAsc();
}
