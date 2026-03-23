package it.f3rren.aquarium.species.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.species.exception.ResourceNotFoundException;
import it.f3rren.aquarium.species.model.Fish;
import it.f3rren.aquarium.species.repository.IFishRepository;

@Service
public class FishService implements IFishService {

    private static final Logger log = LoggerFactory.getLogger(FishService.class);

    private final IFishRepository fishRepository;

    public FishService(IFishRepository fishRepository) {
        this.fishRepository = fishRepository;
    }

    @Transactional(readOnly = true)
    public List<Fish> getAllFish() {
        log.debug("Retrieving all fish species");
        return fishRepository.findAllSortedByName();
    }

    @Transactional(readOnly = true)
    public Fish getFishById(Long id) {
        return fishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fish not found with ID: " + id));
    }
}
