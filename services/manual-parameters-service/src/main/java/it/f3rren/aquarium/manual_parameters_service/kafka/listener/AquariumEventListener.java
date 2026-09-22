package it.f3rren.aquarium.manual_parameters_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.manual_parameters_service.repository.ManualParameterRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final ManualParameterRepository manualParameterRepository;

    public AquariumEventListener(ManualParameterRepository manualParameterRepository) {
        this.manualParameterRepository = manualParameterRepository;
    }

    @Override
    protected String getResourceDescription() {
        return "manual parameters";
    }

    @Override
    protected void handleAquariumDeleted(Long aquariumId) {
        manualParameterRepository.deleteAllByAquariumId(aquariumId);
    }
}
