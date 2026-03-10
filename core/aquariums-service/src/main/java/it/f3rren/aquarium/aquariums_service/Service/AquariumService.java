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

/**
 * Service class for managing Aquarium entities.
 * Provides operations for creating, retrieving, updating, and deleting Aquarium entities.
 * Also handles basic CRUD operations and transactions.
 * Utilizes Spring Data JPA for database interactions.
 * Caches and transactions are managed by Spring.
 * Logs are managed by SLF4J.
 * @Service annotation marks this class as a Spring service component.
 * @author F3rren
 */
@Service
public class AquariumService {

    /**
     * Logger for AquariumService.
     */
    private static final Logger log = LoggerFactory.getLogger(AquariumService.class);

    /**
     * Repository for managing Aquarium entities.
     */
    private final IAquariumRepository aquariumRepository;

    /**
     * Constructor for AquariumService.
     * Initializes the AquariumService with an AquariumRepository.
     * @param aquariumRepository Repository for managing Aquarium entities.
     */
    public AquariumService(IAquariumRepository aquariumRepository) {
        this.aquariumRepository = aquariumRepository;
    }

    /**
     * Creates a new Aquarium entity.
     * Validates the input DTO and saves the Aquarium entity to the database.
     * Logs the creation of the Aquarium entity.
     * @param dto DTO containing Aquarium creation details.
     * @return Created Aquarium entity.
     */
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

    /**
     * Retrieves all Aquarium entities.
     * @return List of all Aquarium entities.
     */
    @Transactional(readOnly = true)
    public List<Aquarium> getAllAquariums() {
        return aquariumRepository.findAll();
    }

    /**
     * Retrieves an Aquarium entity by its ID.
     * @param id ID of the Aquarium entity to retrieve.
     * @return Aquarium entity with the specified ID.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     */
    @Transactional(readOnly = true)
    public Aquarium getAquariumById(Long id) {
        return aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aquarium not found with ID: " + id));
    }

    /**
     * Updates an Aquarium entity with the provided details.
     * Validates the input DTO and updates the Aquarium entity in the database.
     * Logs the update of the Aquarium entity.
     * @param id ID of the Aquarium entity to update.
     * @param dto DTO containing Aquarium update details.
     * @return Updated Aquarium entity.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     */
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

    /**
     * Deletes an Aquarium entity by its ID.
     * Validates the existence of the Aquarium entity and deletes it from the database.
     * Logs the deletion of the Aquarium entity.
     * @param id ID of the Aquarium entity to delete.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     */
    @Transactional
    public void deleteAquarium(Long id) {
        if (!aquariumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aquarium not found with ID: " + id);
        }
        log.info("Deleting aquarium with ID: {}", id);
        aquariumRepository.deleteById(id);
    }
}