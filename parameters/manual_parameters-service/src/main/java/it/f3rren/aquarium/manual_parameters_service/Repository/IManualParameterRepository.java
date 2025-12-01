package it.f3rren.aquarium.manual_parameters_service.Repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.manual_parameters_service.Model.ManualParameter;

public interface IManualParameterRepository extends JpaRepository<ManualParameter, Long> {
    
    List<ManualParameter> findByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);
    
    ManualParameter findFirstByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);
    
    List<ManualParameter> findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
        Long aquariumId, 
        LocalDateTime start, 
        LocalDateTime end
    );
}
