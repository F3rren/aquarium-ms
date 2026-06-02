package it.f3rren.aquarium.aquariums_service.kafka.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import it.f3rren.aquarium.aquariums_service.kafka.config.KafkaTopicConfig;
import it.f3rren.aquarium.aquariums_service.kafka.event.AquariumEvent;

@Component
public class AquariumEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AquariumEventPublisher.class);

    private final KafkaTemplate<String, AquariumEvent> kafkaTemplate;

    public AquariumEventPublisher(KafkaTemplate<String, AquariumEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDeleted(Long aquariumId) {
        AquariumEvent event = new AquariumEvent(aquariumId, "DELETED");
        kafkaTemplate.send(KafkaTopicConfig.AQUARIUM_EVENTS_TOPIC, aquariumId.toString(), event);
        log.info("Published DELETED event for aquarium ID: {}", aquariumId);
    }

    public void publishCreated(Long aquariumId) {
        AquariumEvent event = new AquariumEvent(aquariumId, "CREATED");
        kafkaTemplate.send(KafkaTopicConfig.AQUARIUM_EVENTS_TOPIC, aquariumId.toString(), event);
        log.info("Published CREATED event for aquarium ID: {}", aquariumId);
    }
}
