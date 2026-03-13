package it.f3rren.aquarium.aquariums_service.service;

import java.util.List;

import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;

/**
 * Service interface for managing {@link Aquarium} entities.
 * Defines the contract for CRUD operations on aquariums.
 * Implementations are responsible for business logic and persistence.
 *
 * @author F3rren
 */
public interface IAquariumService {

    /**
     * Creates a new aquarium from the given DTO.
     *
     * @param dto creation data
     * @return the persisted {@link Aquarium}
     */
    Aquarium createAquarium(CreateAquariumDTO dto);

    /**
     * Retrieves all aquariums.
     *
     * @return list of all {@link Aquarium} entities; empty list if none exist
     */
    List<Aquarium> getAllAquariums();

    /**
     * Retrieves a single aquarium by its ID.
     *
     * @param id the aquarium ID
     * @return the matching {@link Aquarium}
     * @throws ResourceNotFoundException if no aquarium with the given ID exists
     */
    Aquarium getAquariumById(Long id);

    /**
     * Partially updates an existing aquarium. Only non-null fields in the DTO are applied.
     *
     * @param id  the aquarium ID
     * @param dto fields to update (null fields are ignored)
     * @return the updated {@link Aquarium}
     * @throws ResourceNotFoundException if no aquarium with the given ID exists
     */
    Aquarium updateAquarium(Long id, UpdateAquariumDTO dto);

    /**
     * Deletes an aquarium by its ID.
     *
     * @param id the aquarium ID
     * @throws ResourceNotFoundException if no aquarium with the given ID exists
     */
    void deleteAquarium(Long id);
}
