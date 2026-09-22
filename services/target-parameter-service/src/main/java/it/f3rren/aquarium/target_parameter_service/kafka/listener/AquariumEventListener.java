package it.f3rren.aquarium.target_parameter_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.target_parameter_service.repository.TargetParameterRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final TargetParameterRepository targetParameterRepository;

    public AquariumEventListener(TargetParameterRepository targetParameterRepository) {
        this.targetParameterRepository = targetParameterRepository;
    }

    @Override
    protected String getResourceDescription() {
        return "target parameters";
    }

    @Override
    protected void handleAquariumDeleted(Long aquariumId) {
        targetParameterRepository.deleteAllByAquariumId(aquariumId);
    }
}
