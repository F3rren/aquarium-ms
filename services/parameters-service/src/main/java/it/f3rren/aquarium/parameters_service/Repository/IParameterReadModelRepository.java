package it.f3rren.aquarium.parameters_service.repository;

import it.f3rren.aquarium.parameters_service.model.ParameterReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IParameterReadModelRepository extends JpaRepository<ParameterReadModel, Long> {

    Optional<ParameterReadModel> findByAquariumId(Long aquariumId);

    void deleteByAquariumId(Long aquariumId);
}
