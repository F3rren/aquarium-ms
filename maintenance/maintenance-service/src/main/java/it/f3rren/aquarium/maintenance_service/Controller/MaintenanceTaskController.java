package it.f3rren.aquarium.maintenance_service.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.f3rren.aquarium.maintenance_service.Model.MaintenanceTask;
import it.f3rren.aquarium.maintenance_service.Service.MaintenanceTaskService;


@RestController
@RequestMapping("api/aquariums")
@CrossOrigin(origins = "*")
public class MaintenanceTaskController {
    
    @Autowired
    private MaintenanceTaskService taskService;
    
    // GET - Lista tutti i task di un acquario
    @GetMapping("/{id}/tasks")
    public ResponseEntity<?> getAllTasks(
            @PathVariable Long id,
            @RequestParam(required = false) String status) {
        
        List<MaintenanceTask> tasks;
        
        if ("pending".equals(status)) {
            tasks = taskService.getPendingTasks(id);
        } else if ("completed".equals(status)) {
            tasks = taskService.getTasksByStatus(id, true);
        } else {
            tasks = taskService.getAllTasks(id);
        }
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task recuperati con successo",
            "data", tasks,
            "metadata", Map.of(
                "aquariumId", id,
                "count", tasks.size()
            )
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // POST - Crea un nuovo task
    @PostMapping("/{id}/tasks")
    public ResponseEntity<?> createTask(
            @PathVariable Long id,
            @RequestBody MaintenanceTask task) {
        
        MaintenanceTask created = taskService.createTask(id, task);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task creato con successo",
            "data", created
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // PUT - Aggiorna un task esistente
    @PutMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            @RequestBody MaintenanceTask task) {
        
        MaintenanceTask updated = taskService.updateTask(taskId, task);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task aggiornato con successo",
            "data", updated
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // DELETE - Elimina un task
    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {
        
        taskService.deleteTask(taskId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task eliminato con successo"
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // POST - Marca un task come completato
    @PostMapping("/{id}/tasks/{taskId}/complete")
    public ResponseEntity<?> completeTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {
        
        MaintenanceTask completed = taskService.completeTask(taskId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task completato con successo",
            "data", completed
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
