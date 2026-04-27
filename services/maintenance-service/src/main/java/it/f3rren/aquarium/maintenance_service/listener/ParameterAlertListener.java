package it.f3rren.aquarium.maintenance_service.listener;

import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Reacts to ParameterMeasured events: if pH or temperature is out of the
 * safe range for a saltwater aquarium, it automatically creates a HIGH-priority
 * maintenance task — no HTTP polling required.
 *
 * Safe ranges used: temperature 24–28°C, pH 8.1–8.4.
 */
@Component
public class ParameterAlertListener {

    private static final Logger log = LoggerFactory.getLogger(ParameterAlertListener.class);

    private static final double TEMP_MIN = 24.0;
    private static final double TEMP_MAX = 28.0;
    private static final double PH_MIN   = 8.1;
    private static final double PH_MAX   = 8.4;

    private final IMaintenanceTaskRepository taskRepository;
    private final ProcessedEventRepository processedEventRepository;

    public ParameterAlertListener(IMaintenanceTaskRepository taskRepository,
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
    @KafkaListener(topics = "parameter.measurements", groupId = "maintenance-alerts")
    @Transactional
    public void onParameterMeasured(String payload) {
        String eventId = extractField(payload, "eventId");
        String aquariumId = extractField(payload, "aggregateId");

        if (eventId == null || aquariumId == null) return;

        if (processedEventRepository.existsByEventId(eventId)) {
            log.debug("Alert event {} already processed, skipping", eventId);
            return;
        }

        Long id = Long.parseLong(aquariumId);
        Double temperature = extractDouble(payload, "temperature");
        Double ph = extractDouble(payload, "ph");

        if (temperature != null && (temperature < TEMP_MIN || temperature > TEMP_MAX)) {
            createAlertTask(id,
                    "Temperature alert",
                    String.format("Temperature %.1f°C is outside safe range (%.0f–%.0f°C). Check heater/chiller.", temperature, TEMP_MIN, TEMP_MAX));
        }

        if (ph != null && (ph < PH_MIN || ph > PH_MAX)) {
            createAlertTask(id,
                    "pH alert",
                    String.format("pH %.2f is outside safe range (%.1f–%.1f). Check alkalinity and buffering.", ph, PH_MIN, PH_MAX));
        }

        processedEventRepository.markProcessed(eventId, Instant.now());
    }

    @DltHandler
    public void handleDlt(String payload) {
        log.error("DLT: could not process parameter alert after retries: {}", payload);
    }

    private void createAlertTask(Long aquariumId, String title, String description) {
        MaintenanceTask task = new MaintenanceTask();
        task.setAquariumId(aquariumId);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority("high");
        task.setFrequency("custom");
        task.setIsCompleted(false);
        task.setDueDate(LocalDateTime.now(ZoneOffset.UTC).plusHours(1));
        taskRepository.save(task);
        log.warn("Alert task created for aquarium {}: {}", aquariumId, title);
    }

    private String extractField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }

    private Double extractDouble(String json, String field) {
        String key = "\"" + field + "\":";
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        if (end == -1) return null;
        try { return Double.parseDouble(json.substring(start, end).trim()); }
        catch (NumberFormatException e) { return null; }
    }
}
