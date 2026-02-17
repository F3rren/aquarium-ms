package it.f3rren.aquarium.aquariums_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.repository.IAquariumRepository;

@Service
public class AquariumService {

    private static final Logger log = LoggerFactory.getLogger(AquariumService.class);

    private final IAquariumRepository aquariumRepository;

    public AquariumService(IAquariumRepository aquariumRepository) {
        this.aquariumRepository = aquariumRepository;
    }

    @Transactional
    public Aquarium createAquarium(CreateAquariumDTO dto) {
        Aquarium aquarium = new Aquarium();
        aquarium.setName(dto.getName().trim());
        aquarium.setVolume(dto.getVolume());
        aquarium.setType(dto.getType());
        aquarium.setDescription(dto.getDescription());
        aquarium.setImageUrl(dto.getImageUrl());

        log.info("Creating aquarium: {}", dto.getName());
        return aquariumRepository.save(aquarium);
    }

    @Transactional(readOnly = true)
    public List<Aquarium> getAllAquariums() {
        return aquariumRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Aquarium getAquariumById(Long id) {
        return aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aquarium not found with ID: " + id));
    }

    @Transactional
    public Aquarium updateAquarium(Long id, UpdateAquariumDTO dto) {
        Aquarium existing = aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aquarium not found with ID: " + id));

        // Update only non-null fields (partial update) — preserves createdAt and other fields
        if (dto.getName() != null) {
            existing.setName(dto.getName().trim());
        }
        if (dto.getVolume() != null) {
            existing.setVolume(dto.getVolume());
        }
        if (dto.getType() != null) {
            existing.setType(dto.getType());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }
        if (dto.getImageUrl() != null) {
            existing.setImageUrl(dto.getImageUrl());
        }

        log.info("Updating aquarium with ID: {}", id);
        return aquariumRepository.save(existing);
    }

    @Transactional
    public void deleteAquarium(Long id) {
        if (!aquariumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aquarium not found with ID: " + id);
        }
        log.info("Deleting aquarium with ID: {}", id);
        aquariumRepository.deleteById(id);
    }
}