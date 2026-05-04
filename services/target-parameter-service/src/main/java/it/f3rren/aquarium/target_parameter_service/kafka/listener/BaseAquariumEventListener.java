package it.f3rren.aquarium.target_parameter_service.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.target_parameter_service.kafka.event.AquariumEvent;

public abstract class BaseAquariumEventListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    @KafkaListener(topics = "aquarium-events", groupId = "${spring.application.name}")
    public final void onAquariumEvent(AquariumEvent event) {
        if ("DELETED".equals(event.type())) {
            log.info("Received DELETED event for aquarium ID: {} — removing {}", event.aquariumId(), getResourceDescription());
            handleAquariumDeleted(event.aquariumId());
        }
    }

    protected abstract String getResourceDescription();

    protected abstract void handleAquariumDeleted(Long aquariumId);
}
