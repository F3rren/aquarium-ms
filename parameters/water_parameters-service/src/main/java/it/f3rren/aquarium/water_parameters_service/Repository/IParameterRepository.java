package it.f3rren.aquarium.water_parameters_service.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.water_parameters_service.Model.Parameter;

public interface IParameterRepository extends JpaRepository<Parameter, Long> {
    
    List<Parameter> findByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);
        
    List<Parameter> findTop10ByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);
        
    Parameter findFirstByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);
        
    List<Parameter> findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
        Long aquariumId, 
        LocalDateTime start, 
        LocalDateTime end
    );
}
