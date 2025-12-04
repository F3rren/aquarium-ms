package it.f3rren.aquarium.inhabitants_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.inhabitants_service.model.Inhabitant;

public interface IInhabitantRepository extends JpaRepository<Inhabitant, Long> {
    List<Inhabitant> findByAquariumId(Long aquariumId);
    List<Inhabitant> findByAquariumIdAndInhabitantType(Long aquariumId, String inhabitantType);
}
