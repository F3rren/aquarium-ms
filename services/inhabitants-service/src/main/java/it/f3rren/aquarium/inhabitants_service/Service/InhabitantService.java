package it.f3rren.aquarium.inhabitants_service.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.inhabitants_service.dto.CoralDTO;
import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.FishDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.inhabitants_service.model.Inhabitant;
import it.f3rren.aquarium.inhabitants_service.model.InhabitantType;
import it.f3rren.aquarium.inhabitants_service.client.SpeciesClient;
import it.f3rren.aquarium.inhabitants_service.mapper.InhabitantMapper;
import it.f3rren.aquarium.inhabitants_service.repository.IInhabitantRepository;

@Service
public class InhabitantService implements IInhabitantService {

    private static final Logger log = LoggerFactory.getLogger(InhabitantService.class);

    private final IInhabitantRepository inhabitantRepository;
    private final SpeciesClient speciesClient;
    private final InhabitantMapper inhabitantMapper;

    public InhabitantService(IInhabitantRepository inhabitantRepository, SpeciesClient speciesClient, InhabitantMapper inhabitantMapper) {
        this.inhabitantRepository = inhabitantRepository;
        this.speciesClient = speciesClient;
        this.inhabitantMapper = inhabitantMapper;
    }

    @Transactional(readOnly = true)
    public List<InhabitantDetailsDTO> getInhabitantsByAquariumId(Long aquariumId) {
        List<Inhabitant> relations = inhabitantRepository.findByAquariumId(aquariumId);
        List<InhabitantDetailsDTO> result = new ArrayList<>();

        for (Inhabitant relation : relations) {
            InhabitantDetailsDTO dto = inhabitantMapper.toDetailsDTO(relation);

            try {
                InhabitantType type = InhabitantType.fromValue(relation.getInhabitantType());
                if (type == InhabitantType.FISH) {
                    FishDTO fish = speciesClient.getFishById(relation.getInhabitantId());
                    dto.setCommonName(relation.getCustomName() != null ? relation.getCustomName() : fish.getCommonName());
                    dto.setScientificName(fish.getScientificName());
                    dto.setDetails(fish);
                } else if (type == InhabitantType.CORAL) {
                    CoralDTO coral = speciesClient.getCoralById(relation.getInhabitantId());
                    dto.setCommonName(relation.getCustomName() != null ? relation.getCustomName() : coral.getCommonName());
                    dto.setScientificName(coral.getScientificName());
                    dto.setDetails(coral);
                }
            } catch (ResourceNotFoundException e) {
                log.warn("Species not found for inhabitant {} (type={}, speciesId={}): {}",
                        relation.getId(), relation.getInhabitantType(), relation.getInhabitantId(), e.getMessage());
            } catch (RuntimeException e) {
                log.error("Error fetching species details for inhabitant {}: {}",
                        relation.getId(), e.getMessage());
            }

            result.add(dto);
        }

        return result;
    }

    @Transactional
    public InhabitantDetailsDTO addInhabitant(Long aquariumId, CreateInhabitantDTO dto) {
        InhabitantType type = dto.getInhabitantType();
        if (type == InhabitantType.FISH) {
            speciesClient.getFishById(dto.getInhabitantId());
        } else {
            speciesClient.getCoralById(dto.getInhabitantId());
        }

        Inhabitant inhabitant = new Inhabitant();
        inhabitant.setAquariumId(aquariumId);
        inhabitant.setInhabitantType(type.getValue());
        inhabitant.setInhabitantId(dto.getInhabitantId());
        inhabitant.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        inhabitant.setNotes(dto.getNotes());
        inhabitant.setCustomName(dto.getCustomName());

        log.info("Adding {} (speciesId={}) to aquarium {}", type.getValue(), dto.getInhabitantId(), aquariumId);
        return inhabitantMapper.toDetailsDTO(inhabitantRepository.save(inhabitant));
    }

    @Transactional
    public void removeInhabitant(Long aquariumId, Long inhabitantId) {
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inhabitant not found with ID: " + inhabitantId));

        if (!inhabitant.getAquariumId().equals(aquariumId)) {
            throw new IllegalArgumentException(
                    "Inhabitant " + inhabitantId + " does not belong to aquarium " + aquariumId);
        }

        log.info("Removing inhabitant {} from aquarium {}", inhabitantId, aquariumId);
        inhabitantRepository.deleteById(inhabitantId);
    }

    @Transactional
    public InhabitantDetailsDTO updateInhabitant(Long aquariumId, Long inhabitantId, UpdateInhabitantDTO dto) {
        Inhabitant existing = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inhabitant not found with ID: " + inhabitantId));

        if (!existing.getAquariumId().equals(aquariumId)) {
            throw new IllegalArgumentException(
                    "Inhabitant " + inhabitantId + " does not belong to aquarium " + aquariumId);
        }

        if (dto.getQuantity() != null) existing.setQuantity(dto.getQuantity());
        if (dto.getNotes() != null) existing.setNotes(dto.getNotes());
        if (dto.getCustomName() != null) existing.setCustomName(dto.getCustomName());
        if (dto.getCurrentSize() != null) existing.setCurrentSize(dto.getCurrentSize());
        if (dto.getCustomDifficulty() != null) existing.setCustomDifficulty(dto.getCustomDifficulty());
        if (dto.getCustomTemperament() != null) existing.setCustomTemperament(dto.getCustomTemperament());
        if (dto.getCustomDiet() != null) existing.setCustomDiet(dto.getCustomDiet());
        if (dto.getIsReefSafe() != null) existing.setIsReefSafe(dto.getIsReefSafe());
        if (dto.getCustomMinTankSize() != null) existing.setCustomMinTankSize(dto.getCustomMinTankSize());

        log.info("Updating inhabitant {} in aquarium {}", inhabitantId, aquariumId);
        return inhabitantMapper.toDetailsDTO(inhabitantRepository.save(existing));
    }
}
