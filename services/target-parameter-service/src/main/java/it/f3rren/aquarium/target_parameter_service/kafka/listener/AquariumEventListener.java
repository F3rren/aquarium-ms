package it.f3rren.aquarium.target_parameter_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.target_parameter_service.repository.ITargetParameterRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final ITargetParameterRepository targetParameterRepository;

    public AquariumEventListener(ITargetParameterRepository targetParameterRepository) {
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
