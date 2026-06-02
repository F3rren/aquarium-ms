package it.f3rren.aquarium.target_parameter_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.f3rren.aquarium.target_parameter_service.model.TargetParameter;

@Repository
public interface ITargetParameterRepository extends JpaRepository<TargetParameter, Long> {
    Optional<TargetParameter> findByAquariumId(Long aquariumId);

    void deleteAllByAquariumId(Long aquariumId);
}
