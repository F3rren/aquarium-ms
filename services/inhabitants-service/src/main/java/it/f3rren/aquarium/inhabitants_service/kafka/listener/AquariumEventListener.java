package it.f3rren.aquarium.inhabitants_service.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.inhabitants_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.inhabitants_service.repository.IInhabitantRepository;

@Component
public class AquariumEventListener {

    private static final Logger log = LoggerFactory.getLogger(AquariumEventListener.class);

    private final IInhabitantRepository inhabitantRepository;

    public AquariumEventListener(IInhabitantRepository inhabitantRepository) {
        this.inhabitantRepository = inhabitantRepository;
    }

    @Transactional
    @KafkaListener(topics = "aquarium-events", groupId = "${spring.application.name}")
    public void onAquariumEvent(AquariumEvent event) {
        if ("DELETED".equals(event.type())) {
            log.info("Received DELETED event for aquarium ID: {} — removing inhabitants", event.aquariumId());
            inhabitantRepository.deleteAllByAquariumId(event.aquariumId());
        }
    }
}
