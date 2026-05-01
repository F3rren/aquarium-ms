package it.f3rren.aquarium.parameters_service.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.parameters_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.parameters_service.repository.IParameterRepository;

@Component
public class AquariumEventListener {

    private static final Logger log = LoggerFactory.getLogger(AquariumEventListener.class);

    private final IParameterRepository parameterRepository;

    public AquariumEventListener(IParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Transactional
    @KafkaListener(topics = "aquarium-events", groupId = "${spring.application.name}")
    public void onAquariumEvent(AquariumEvent event) {
        if ("DELETED".equals(event.type())) {
            log.info("Received DELETED event for aquarium ID: {} — removing water parameters", event.aquariumId());
            parameterRepository.deleteAllByAquariumId(event.aquariumId());
        }
    }
}
