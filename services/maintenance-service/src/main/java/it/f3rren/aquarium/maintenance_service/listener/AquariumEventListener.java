package it.f3rren.aquarium.maintenance_service.listener;

import it.f3rren.aquarium.maintenance_service.repository.IMaintenanceTaskRepository;
import it.f3rren.aquarium.maintenance_service.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class AquariumEventListener {

    private static final Logger log = LoggerFactory.getLogger(AquariumEventListener.class);

    private final IMaintenanceTaskRepository taskRepository;
    private final ProcessedEventRepository processedEventRepository;

    public AquariumEventListener(IMaintenanceTaskRepository taskRepository,
                                  ProcessedEventRepository processedEventRepository) {
        this.taskRepository = taskRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(topics = "aquarium.lifecycle", groupId = "maintenance-service")
    @Transactional
    public void onAquariumEvent(String payload) {
        if (!payload.contains("\"AquariumDeleted\"")) return;

        String eventId = extractField(payload, "eventId");
        String aquariumId = extractField(payload, "aggregateId");

        if (eventId == null || aquariumId == null) return;

        if (processedEventRepository.existsByEventId(eventId)) {
            log.debug("Event {} already processed, skipping", eventId);
            return;
        }

        log.info("Processing AquariumDeleted: removing tasks for aquarium {}", aquariumId);
        taskRepository.deleteAllByAquariumId(Long.parseLong(aquariumId));
        processedEventRepository.markProcessed(eventId, Instant.now());
    }

    @DltHandler
    public void handleDlt(String payload) {
        log.error("DLT: could not process aquarium lifecycle event after retries: {}", payload);
    }

    private String extractField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }
}
