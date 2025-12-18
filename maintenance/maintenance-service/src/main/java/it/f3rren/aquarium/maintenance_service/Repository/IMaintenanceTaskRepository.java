package it.f3rren.aquarium.maintenance_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;

public interface IMaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {
    
    List<MaintenanceTask> findByAquariumIdOrderByDueDateAsc(Long aquariumId);
    
    List<MaintenanceTask> findByAquariumIdAndIsCompletedOrderByDueDateAsc(Long aquariumId, Boolean isCompleted);
    
    List<MaintenanceTask> findByAquariumIdAndIsCompletedFalseOrderByDueDateAsc(Long aquariumId);
}
