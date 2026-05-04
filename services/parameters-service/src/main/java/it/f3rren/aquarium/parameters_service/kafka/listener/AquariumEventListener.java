package it.f3rren.aquarium.parameters_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.parameters_service.repository.IParameterRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final IParameterRepository parameterRepository;

    public AquariumEventListener(IParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Override
    protected String getResourceDescription() {
        return "water parameters";
    }

    @Override
    protected void handleAquariumDeleted(Long aquariumId) {
        parameterRepository.deleteAllByAquariumId(aquariumId);
    }
}
