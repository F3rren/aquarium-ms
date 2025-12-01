package it.f3rren.aquarium.target_parameter_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.f3rren.aquarium.target_parameter_service.Model.TargetParameter;

@Repository
public interface ITargetParameterRepository extends JpaRepository<TargetParameter, Long> {
    TargetParameter findByAquariumId(Long aquariumId);
}
