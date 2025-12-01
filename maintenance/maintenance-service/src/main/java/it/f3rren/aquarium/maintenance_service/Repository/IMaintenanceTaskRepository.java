package it.f3rren.aquarium.maintenance_service.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import it.f3rren.aquarium.maintenance_service.Model.MaintenanceTask;

public interface IMaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {
    
    List<MaintenanceTask> findByAquariumIdOrderByDueDateAsc(Long aquariumId);
    
    List<MaintenanceTask> findByAquariumIdAndIsCompletedOrderByDueDateAsc(Long aquariumId, Boolean isCompleted);
    
    List<MaintenanceTask> findByAquariumIdAndIsCompletedFalseOrderByDueDateAsc(Long aquariumId);
}
