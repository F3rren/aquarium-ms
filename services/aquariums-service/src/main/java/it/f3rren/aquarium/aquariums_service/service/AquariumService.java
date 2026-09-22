package it.f3rren.aquarium.aquariums_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.exception.ForbiddenException;
import it.f3rren.aquarium.aquariums_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;

/**
 * Service interface for managing {@link Aquarium} entities.
 * Defines the contract for CRUD operations on aquariums.
 * Implementations are responsible for business logic and persistence.
 *
 * @author Samuele Alessandro Di Silvestri
 */
public interface AquariumService {

    /**
     * Creates a new aquarium from the given DTO, owned by the given user.
     *
     * @param dto     creation data
     * @param ownerId id of the user creating this aquarium (from the gateway-injected
     *                {@code X-User-Id} header, never from client-supplied request data)
     * @return the persisted {@link Aquarium}
     */
    Aquarium createAquarium(CreateAquariumDTO dto, Long ownerId);

    /**
     * Retrieves a paginated list of aquariums.
     *
     * @param pageable pagination and sorting parameters
     * @return page of {@link Aquarium} entities
     */
    Page<Aquarium> getAllAquariums(Pageable pageable);

    /**
     * Retrieves a single aquarium by its ID.
     * <p>
     * Deliberately does not check ownership - anyone who can reach this endpoint (i.e. is
     * authenticated at the gateway) can read any aquarium by id, regardless of who created it.
     * This is an intentional IDOR test fixture: {@link #updateAquarium} and
     * {@link #deleteAquarium} both enforce ownership, so a security scan against this API can
     * demonstrate the difference between an endpoint that checks authorization and one that
     * only checks authentication.
     *
     * @param id the aquarium ID
     * @return the matching {@link Aquarium}
     * @throws ResourceNotFoundException if no aquarium with the given ID exists
     */
    Aquarium getAquariumById(Long id);

    /**
     * Partially updates an existing aquarium. Only non-null fields in the DTO are applied.
     *
     * @param id      the aquarium ID
     * @param dto     fields to update (null fields are ignored)
     * @param ownerId id of the caller (from {@code X-User-Id}); must match the aquarium's owner
     * @return the updated {@link Aquarium}
     * @throws ResourceNotFoundException if no aquarium with the given ID exists
     * @throws ForbiddenException        if the caller does not own the aquarium
     */
    Aquarium updateAquarium(Long id, UpdateAquariumDTO dto, Long ownerId);

    /**
     * Deletes an aquarium by its ID.
     *
     * @param id      the aquarium ID
     * @param ownerId id of the caller (from {@code X-User-Id}); must match the aquarium's owner
     * @throws ResourceNotFoundException if no aquarium with the given ID exists
     * @throws ForbiddenException        if the caller does not own the aquarium
     */
    void deleteAquarium(Long id, Long ownerId);
}
