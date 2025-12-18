package it.f3rren.aquarium.aquariums_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.aquariums_service.model.Aquarium;

public interface IAquariumRepository extends JpaRepository<Aquarium, Long> {

}
