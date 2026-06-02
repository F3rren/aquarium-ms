package it.f3rren.aquarium.inhabitants_service.kafka.listener;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.inhabitants_service.repository.IInhabitantRepository;

@Component
public class AquariumEventListener extends BaseAquariumEventListener {

    private final IInhabitantRepository inhabitantRepository;

    public AquariumEventListener(IInhabitantRepository inhabitantRepository) {
        this.inhabitantRepository = inhabitantRepository;
    }

    @Override
    protected String getResourceDescription() {
        return "inhabitants";
    }

    @Override
    protected void handleAquariumDeleted(Long aquariumId) {
        inhabitantRepository.deleteAllByAquariumId(aquariumId);
    }
}
