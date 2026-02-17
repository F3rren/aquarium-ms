package it.f3rren.aquarium.maintenance_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import it.f3rren.aquarium.maintenance_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.maintenance_service.dto.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.model.MaintenanceTask;
import it.f3rren.aquarium.maintenance_service.service.MaintenanceTaskService;

@RestController
@RequestMapping("/aquariums/{id}/tasks")
@Tag(name = "MaintenanceTask", description = "API for managing maintenance tasks")
public class MaintenanceTaskController {

    private final MaintenanceTaskService taskService;

    public MaintenanceTaskController(MaintenanceTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all maintenance tasks for an aquarium", description = "Retrieve tasks, optionally filtered by status")
    public ResponseEntity<ApiResponseDTO<List<MaintenanceTask>>> getAllTasks(
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

        ApiResponseDTO<List<MaintenanceTask>> response = new ApiResponseDTO<>(
                true,
                "Tasks retrieved successfully",
                tasks,
                Map.of("aquariumId", id, "count", tasks.size()));

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new maintenance task", description = "Create a new task for a specific aquarium")
    public ResponseEntity<ApiResponseDTO<MaintenanceTask>> createTask(
            @PathVariable Long id,
            @Valid @RequestBody CreateMaintenanceTaskDTO dto) {

        MaintenanceTask created = taskService.createTask(id, dto);

        ApiResponseDTO<MaintenanceTask> response = new ApiResponseDTO<>(
                true,
                "Task created successfully",
                created,
                null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update a maintenance task", description = "Update details of a specific task")
    public ResponseEntity<ApiResponseDTO<MaintenanceTask>> updateTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateMaintenanceTaskDTO dto) {

        MaintenanceTask updated = taskService.updateTask(id, taskId, dto);

        ApiResponseDTO<MaintenanceTask> response = new ApiResponseDTO<>(
                true,
                "Task updated successfully",
                updated,
                null);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a maintenance task", description = "Delete a specific task")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {

        taskService.deleteTask(id, taskId);

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(
                true,
                "Task deleted successfully",
                null,
                null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Mark a task as completed", description = "Mark a specific task as completed")
    public ResponseEntity<ApiResponseDTO<MaintenanceTask>> completeTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {

        MaintenanceTask completed = taskService.completeTask(id, taskId);

        ApiResponseDTO<MaintenanceTask> response = new ApiResponseDTO<>(
                true,
                "Task completed successfully",
                completed,
                null);

        return ResponseEntity.ok(response);
    }
}
