package it.f3rren.aquarium.maintenance_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.maintenance_service.repository.MaintenanceTaskRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public AquariumEventListener(MaintenanceTaskRepository maintenanceTaskRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
    }

    @Override
    protected String getResourceDescription() {
        return "maintenance tasks";
    }

    @Override
    protected void handleAquariumDeleted(Long aquariumId) {
        maintenanceTaskRepository.deleteAllByAquariumId(aquariumId);
    }
}
