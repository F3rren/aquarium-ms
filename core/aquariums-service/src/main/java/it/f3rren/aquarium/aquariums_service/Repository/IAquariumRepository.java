package it.f3rren.aquarium.aquariums_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.aquariums_service.model.Aquarium;

/**
 * Repository interface for managing Aquarium entities.
 * Provides CRUD operations for Aquarium entities.
 */
public interface IAquariumRepository extends JpaRepository<Aquarium, Long> {

}
