package it.f3rren.aquarium.aquariums_service.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.aquariums_service.kafka.publisher.AquariumEventPublisher;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.repository.IAquariumRepository;

/**
 * Default implementation of {@link IAquariumService}.
 *
 * <p>Handles all business logic for aquarium lifecycle management: creation with input
 * sanitization (trim), partial updates via {@link java.util.Optional}, and existence
 * checks before delete. All write operations are wrapped in a transaction; reads use
 * {@code readOnly = true} for performance.</p>
 *
 * @author F3rren
 */
@Service
public class AquariumService implements IAquariumService {

    private static final Logger log = LoggerFactory.getLogger(AquariumService.class);

    private final IAquariumRepository aquariumRepository;
    private final AquariumEventPublisher eventPublisher;

    public AquariumService(IAquariumRepository aquariumRepository, AquariumEventPublisher eventPublisher) {
        this.aquariumRepository = aquariumRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new Aquarium entity.
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

        Aquarium saved = aquariumRepository.save(aquarium);
        log.info("Aquarium created with ID: {}", saved.getId());

        // The event publish must never compromise the committed write. A failure here
        // is logged explicitly so the inconsistency is visible instead of silent.
        try {
            eventPublisher.publishCreated(saved.getId());
        } catch (Exception ex) {
            log.error("Failed to publish CREATED event for aquarium ID: {}", saved.getId(), ex);
        }
        return saved;
    }

    /**
     * Retrieves a paginated list of Aquarium entities.
     * @param pageable pagination and sorting parameters.
     * @return Page of Aquarium entities.
     */
    @Transactional(readOnly = true)
    public Page<Aquarium> getAllAquariums(Pageable pageable) {
        return aquariumRepository.findAll(pageable);
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
     * Only non-null fields in the DTO are applied (partial update).
     * @param id ID of the Aquarium entity to update.
     * @param dto DTO containing Aquarium update details.
     * @return Updated Aquarium entity.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     */
    @Transactional
    public Aquarium updateAquarium(Long id, UpdateAquariumDTO dto) {
        Aquarium existing = aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aquarium not found with ID: " + id));

        // Partial update: only non-null fields are applied, preserving existing values
        Optional.ofNullable(dto.getName()).map(String::trim).ifPresent(existing::setName);
        Optional.ofNullable(dto.getVolume()).ifPresent(existing::setVolume);
        Optional.ofNullable(dto.getType()).ifPresent(existing::setType);
        Optional.ofNullable(dto.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(dto.getImageUrl()).ifPresent(existing::setImageUrl);

        Aquarium updated = aquariumRepository.save(existing);
        log.info("Aquarium updated with ID: {}", updated.getId());
        return updated;
    }

    /**
     * Deletes an Aquarium entity by its ID.
     * @param id ID of the Aquarium entity to delete.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     */
    @Transactional
    public void deleteAquarium(Long id) {
        if (!aquariumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aquarium not found with ID: " + id);
        }
        aquariumRepository.deleteById(id);
        log.info("Aquarium deleted with ID: {}", id);

        // As with create, a publish failure must not hide the fact that the row was deleted.
        try {
            eventPublisher.publishDeleted(id);
        } catch (Exception ex) {
            log.error("Failed to publish DELETED event for aquarium ID: {}", id, ex);
        }
    }
}
