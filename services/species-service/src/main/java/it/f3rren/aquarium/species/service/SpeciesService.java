package it.f3rren.aquarium.species.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.species.dto.CoralResponseDTO;
import it.f3rren.aquarium.species.dto.FishResponseDTO;
import it.f3rren.aquarium.species.exception.ResourceNotFoundException;
import it.f3rren.aquarium.species.model.Coral;
import it.f3rren.aquarium.species.model.Fish;
import it.f3rren.aquarium.species.repository.ICoralRepository;
import it.f3rren.aquarium.species.repository.IFishRepository;

@Service
public class SpeciesService implements ISpeciesService {

    private static final Logger log = LoggerFactory.getLogger(SpeciesService.class);

    private final IFishRepository fishRepository;
    private final ICoralRepository coralRepository;

    public SpeciesService(IFishRepository fishRepository, ICoralRepository coralRepository) {
        this.fishRepository = fishRepository;
        this.coralRepository = coralRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FishResponseDTO> getAllFish() {
        log.debug("Retrieving all fish species");
        return fishRepository.findAllSortedByName().stream()
                .map(this::toFishDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FishResponseDTO getFishById(Long id) {
        Fish fish = fishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fish not found with ID: " + id));
        return toFishDTO(fish);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoralResponseDTO> getAllCorals() {
        log.debug("Retrieving all coral species");
        return coralRepository.findAllSortedByName().stream()
                .map(this::toCoralDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CoralResponseDTO getCoralById(Long id) {
        Coral coral = coralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coral not found with ID: " + id));
        return toCoralDTO(coral);
    }

    private FishResponseDTO toFishDTO(Fish fish) {
        return new FishResponseDTO(
                fish.getId(),
                fish.getCommonName(),
                fish.getScientificName(),
                fish.getFamily(),
                fish.getMinTankSize(),
                fish.getMaxSize(),
                fish.getDifficulty(),
                fish.isReefSafe(),
                fish.getTemperament(),
                fish.getDiet(),
                fish.getImageUrl(),
                fish.getDescription(),
                fish.getWaterType());
    }

    private CoralResponseDTO toCoralDTO(Coral coral) {
        return new CoralResponseDTO(
                coral.getId(),
                coral.getCommonName(),
                coral.getScientificName(),
                coral.getType(),
                coral.getMinTankSize(),
                coral.getMaxSize(),
                coral.getDifficulty(),
                coral.getLightRequirement(),
                coral.getFlowRequirement(),
                coral.getPlacement(),
                coral.isAggressive(),
                coral.getFeeding(),
                coral.getDescription());
    }
}
