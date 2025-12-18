package it.f3rren.aquarium.maintenance_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;
import it.f3rren.aquarium.maintenance_service.service.MaintenanceTaskService;


@RestController
@RequestMapping("/aquariums/{id}/tasks")
@Tag(name = "MaintenanceTask", description = "API for managing maintenance tasks")
public class MaintenanceTaskController {
    
    @Autowired
    private MaintenanceTaskService taskService;
    
    // GET - Lista tutti i task di un acquario
    @GetMapping
    @Operation(summary = "Get all maintenance tasks for an aquarium", description = "Retrieve a list of all maintenance tasks for a specific aquarium, optionally filtered by status")
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
            "message", "Tasks retrieved successfully",
            "data", tasks,
            "metadata", Map.of(
                "aquariumId", id,
                "count", tasks.size()
            )
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // POST - Create a new task
    @PostMapping
    @Operation(summary = "Create a new maintenance task for an aquarium", description = "Create a new maintenance task for a specific aquarium")
    public ResponseEntity<?> createTask(
            @PathVariable Long id,
            @RequestBody MaintenanceTask task) {
        
        MaintenanceTask created = taskService.createTask(id, task);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task created successfully",
            "data", created
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // PUT - Update an existing task
    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing maintenance task for an aquarium", description = "Update details of a specific maintenance task for a specific aquarium")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            @RequestBody MaintenanceTask task) {
        
        MaintenanceTask updated = taskService.updateTask(taskId, task);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task updated successfully",
            "data", updated
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // DELETE - Delete a task
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a maintenance task for an aquarium", description = "Delete a specific maintenance task for a specific aquarium")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {
        
        taskService.deleteTask(taskId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task deleted successfully"
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // POST - Mark a task as completed
    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Mark a maintenance task as completed for an aquarium", description = "Mark a specific maintenance task as completed for a specific aquarium")
    public ResponseEntity<?> completeTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {
        
        MaintenanceTask completed = taskService.completeTask(taskId);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Task completed successfully",
            "data", completed
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
