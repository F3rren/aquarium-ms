package it.f3rren.aquarium.manual_parameters_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.manual_parameters_service.model.ManualParameter;

public interface IManualParameterRepository extends JpaRepository<ManualParameter, Long> {

    List<ManualParameter> findByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);

    Optional<ManualParameter> findFirstByAquariumIdOrderByMeasuredAtDesc(Long aquariumId);
    
    List<ManualParameter> findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
        Long aquariumId,
        LocalDateTime start,
        LocalDateTime end
    );

    void deleteAllByAquariumId(Long aquariumId);
}
