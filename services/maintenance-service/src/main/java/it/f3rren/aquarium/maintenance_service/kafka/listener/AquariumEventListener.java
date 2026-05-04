package it.f3rren.aquarium.maintenance_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.maintenance_service.repository.IMaintenanceTaskRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final IMaintenanceTaskRepository maintenanceTaskRepository;

    public AquariumEventListener(IMaintenanceTaskRepository maintenanceTaskRepository) {
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
