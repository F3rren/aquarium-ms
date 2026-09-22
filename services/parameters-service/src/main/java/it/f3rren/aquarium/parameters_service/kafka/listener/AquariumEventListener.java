package it.f3rren.aquarium.parameters_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.parameters_service.repository.ParameterRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final ParameterRepository parameterRepository;

    public AquariumEventListener(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Override
    public String getResourceDescription() {
        return "water parameters";
    }

    @Override
    protected void handleAquariumDeleted(Long aquariumId) {
        parameterRepository.deleteAllByAquariumId(aquariumId);
    }
}
