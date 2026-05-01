package it.f3rren.aquarium.maintenance_service.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.maintenance_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.maintenance_service.repository.IMaintenanceTaskRepository;

@Component
public class AquariumEventListener {

    private static final Logger log = LoggerFactory.getLogger(AquariumEventListener.class);

    private final IMaintenanceTaskRepository maintenanceTaskRepository;

    public AquariumEventListener(IMaintenanceTaskRepository maintenanceTaskRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
    }

    @Transactional
    @KafkaListener(topics = "aquarium-events", groupId = "${spring.application.name}")
    public void onAquariumEvent(AquariumEvent event) {
        if ("DELETED".equals(event.type())) {
            log.info("Received DELETED event for aquarium ID: {} — removing maintenance tasks", event.aquariumId());
            maintenanceTaskRepository.deleteAllByAquariumId(event.aquariumId());
        }
    }
}
