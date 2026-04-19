package it.f3rren.aquarium.parameters_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.parameters_service.model.Parameter;

public interface IParameterRepository extends JpaRepository<Parameter, Long> {
    
    List<Parameter> findByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);

    Optional<Parameter> findFirstByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);
        
    List<Parameter> findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
        Long aquariumId, 
        LocalDateTime start, 
        LocalDateTime end
    );
}
