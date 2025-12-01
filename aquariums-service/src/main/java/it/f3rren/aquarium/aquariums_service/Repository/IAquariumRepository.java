package it.f3rren.aquarium.aquariums_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.f3rren.aquarium.aquariums_service.Model.Aquarium;

public interface IAquariumRepository extends JpaRepository<Aquarium, Long> {

}
