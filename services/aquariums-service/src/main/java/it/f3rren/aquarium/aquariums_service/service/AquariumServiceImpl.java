package it.f3rren.aquarium.aquariums_service.service;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.exception.ForbiddenException;
import it.f3rren.aquarium.aquariums_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.repository.AquariumRepository;

/**
 * Default implementation of {@link AquariumService}.
 *
 * <p>
 * Handles all business logic for aquarium lifecycle management: creation with
 * input
 * sanitization (trim), partial updates via {@link java.util.Optional}, and
 * existence
 * checks before delete. All write operations are wrapped in a transaction;
 * reads use
 * {@code readOnly = true} for performance.
 * </p>
 *
 * @author Samuele Alessandro Di Silvestri
 */
@Service
public class AquariumServiceImpl implements AquariumService {

    private static final Logger log = LoggerFactory.getLogger(AquariumServiceImpl.class);

    private final AquariumRepository aquariumRepository;

    public AquariumServiceImpl(AquariumRepository aquariumRepository) {
        this.aquariumRepository = aquariumRepository;
    }

    /**
     * Creates a new Aquarium entity.
     * 
     * @param dto     DTO containing Aquarium creation details.
     * @param ownerId id of the user creating this aquarium.
     * @return Created Aquarium entity.
     */
    @Override
    @Transactional
    public Aquarium createAquarium(CreateAquariumDTO dto, Long ownerId) {
        Aquarium aquarium = new Aquarium();
        aquarium.setName(dto.getName().trim());
        aquarium.setVolume(dto.getVolume());
        aquarium.setType(dto.getType());
        aquarium.setDescription(dto.getDescription());
        aquarium.setImageUrl(dto.getImageUrl());
        aquarium.setOwnerId(ownerId);
        aquarium.setVerified(false);

        Aquarium saved = aquariumRepository.save(aquarium);
        log.info("Aquarium created with ID: {}", saved.getId());

        return saved;
    }

    /**
     * Retrieves a paginated list of Aquarium entities.
     * 
     * @param pageable pagination and sorting parameters.
     * @return Page of Aquarium entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Aquarium> getAllAquariums(Pageable pageable) {
        return aquariumRepository.findAll(pageable);
    }

    /**
     * Retrieves an Aquarium entity by its ID.
     * 
     * @param id ID of the Aquarium entity to retrieve.
     * @return Aquarium entity with the specified ID.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Aquarium getAquariumById(Long id) {

        return aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aquarium not found with ID: "
                                + id));
    }

    /**
     * Updates an Aquarium entity with the provided details.
     * Only non-null fields in the DTO are applied (partial update).
     * 
     * @param id      ID of the Aquarium entity to update.
     * @param dto     DTO containing Aquarium update details.
     * @param ownerId id of the caller; must match the aquarium's owner.
     * @return Updated Aquarium entity.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     * @throws ForbiddenException        if the caller does not own the aquarium.
     */
    @Override
    @Transactional
    public Aquarium updateAquarium(Long id, UpdateAquariumDTO dto, Long ownerId) {
        Aquarium existing = aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aquarium not found with ID: " + id));
        requireOwnership(existing, ownerId);

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
     * 
     * @param id      ID of the Aquarium entity to delete.
     * @param ownerId id of the caller; must match the aquarium's owner.
     * @throws ResourceNotFoundException if the Aquarium entity is not found.
     * @throws ForbiddenException        if the caller does not own the aquarium.
     */
    @Override
    @Transactional
    public void deleteAquarium(
            Long id,
            Long ownerId) {

        Aquarium existing = aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aquarium not found with ID: "
                                + id));

        requireOwnership(existing, ownerId);

        aquariumRepository.delete(existing);

        log.info("Aquarium deleted with ID: {}", id);
    }

    /**
     * Denies write access to an aquarium the caller doesn't own.
     * {@link #getAquariumById}
     * deliberately does not call this - see its javadoc.
     */
    private void requireOwnership(
            Aquarium aquarium,
            Long ownerId) {

        if (!Objects.equals(
                aquarium.getOwnerId(),
                ownerId)) {
            throw new ForbiddenException(
                    "Aquarium " + aquarium.getId()
                            + " is not owned by user "
                            + ownerId);
        }
    }
}
