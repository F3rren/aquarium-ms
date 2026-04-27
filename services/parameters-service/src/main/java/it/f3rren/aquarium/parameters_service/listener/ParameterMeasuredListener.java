package it.f3rren.aquarium.parameters_service.listener;

import it.f3rren.aquarium.parameters_service.model.ParameterReadModel;
import it.f3rren.aquarium.parameters_service.repository.IParameterReadModelRepository;
import it.f3rren.aquarium.parameters_service.repository.ProcessedEventRepository;
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
 * CQRS Read Side: aggiorna parameter_latest ogni volta che arriva
 * un nuovo evento ParameterMeasured su Kafka.
 * GET /latest legge da questa tabella invece di eseguire MAX(measured_at).
 */
@Component
public class ParameterMeasuredListener {

    private static final Logger log = LoggerFactory.getLogger(ParameterMeasuredListener.class);

    private final IParameterReadModelRepository readModelRepository;
    private final ProcessedEventRepository processedEventRepository;

    public ParameterMeasuredListener(IParameterReadModelRepository readModelRepository,
                                      ProcessedEventRepository processedEventRepository) {
        this.readModelRepository = readModelRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(topics = "parameter.measurements", groupId = "parameters-cqrs")
    @Transactional
    public void onParameterMeasured(String payload) {
        String eventId = extractField(payload, "eventId");
        String aquariumId = extractField(payload, "aggregateId");

        if (eventId == null || aquariumId == null) return;

        if (processedEventRepository.existsByEventId(eventId)) {
            log.debug("CQRS event {} already applied, skipping", eventId);
            return;
        }

        Long id = Long.parseLong(aquariumId);
        ParameterReadModel model = readModelRepository.findByAquariumId(id)
                .orElseGet(() -> { ParameterReadModel m = new ParameterReadModel(); m.setAquariumId(id); return m; });

        model.setTemperature(extractDouble(payload, "temperature"));
        model.setPh(extractDouble(payload, "ph"));
        model.setSalinity(extractInt(payload, "salinity"));
        model.setOrp(extractInt(payload, "orp"));
        model.setMeasuredAt(parseInstant(extractField(payload, "measuredAt")));
        model.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

        readModelRepository.save(model);
        processedEventRepository.markProcessed(eventId, Instant.now());
        log.debug("CQRS read model updated for aquarium {}", id);
    }

    @DltHandler
    public void handleDlt(String payload) {
        log.error("DLT: could not update CQRS read model after retries: {}", payload);
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

    private Integer extractInt(String json, String field) {
        Double d = extractDouble(json, field);
        return d != null ? d.intValue() : null;
    }

    private LocalDateTime parseInstant(String value) {
        if (value == null) return LocalDateTime.now(ZoneOffset.UTC);
        try { return LocalDateTime.ofInstant(Instant.parse(value), ZoneOffset.UTC); }
        catch (Exception e) { return LocalDateTime.now(ZoneOffset.UTC); }
    }
}
