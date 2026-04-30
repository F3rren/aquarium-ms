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
import it.f3rren.aquarium.maintenance_service.dto.request.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.response.MaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.service.IMaintenanceTaskService;

@RestController
@RequestMapping("/aquariums/{id}/tasks")
@Tag(name = "MaintenanceTask", description = "API for managing maintenance tasks")
public class MaintenanceTaskController {

    private final IMaintenanceTaskService taskService;

    public MaintenanceTaskController(IMaintenanceTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all maintenance tasks for an aquarium", description = "Retrieve tasks, optionally filtered by status")
    public ResponseEntity<ApiResponseDTO<List<MaintenanceTaskDTO>>> getAllTasks(
            @PathVariable Long id,
            @RequestParam(required = false) String status) {

        List<MaintenanceTaskDTO> tasks;

        if ("pending".equals(status)) {
            tasks = taskService.getPendingTasks(id);
        } else if ("completed".equals(status)) {
            tasks = taskService.getTasksByStatus(id, true);
        } else {
            tasks = taskService.getAllTasks(id);
        }

        ApiResponseDTO<List<MaintenanceTaskDTO>> response = new ApiResponseDTO<>(
                true,
                "Tasks retrieved successfully",
                tasks,
                Map.of("aquariumId", id, "count", tasks.size()));

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new maintenance task", description = "Create a new task for a specific aquarium")
    public ResponseEntity<ApiResponseDTO<MaintenanceTaskDTO>> createTask(
            @PathVariable Long id,
            @Valid @RequestBody CreateMaintenanceTaskDTO dto) {

        MaintenanceTaskDTO created = taskService.createTask(id, dto);

        ApiResponseDTO<MaintenanceTaskDTO> response = new ApiResponseDTO<>(
                true,
                "Task created successfully",
                created,
                null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update a maintenance task", description = "Update details of a specific task")
    public ResponseEntity<ApiResponseDTO<MaintenanceTaskDTO>> updateTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateMaintenanceTaskDTO dto) {

        MaintenanceTaskDTO updated = taskService.updateTask(id, taskId, dto);

        ApiResponseDTO<MaintenanceTaskDTO> response = new ApiResponseDTO<>(
                true,
                "Task updated successfully",
                updated,
                null);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a maintenance task", description = "Delete a specific task")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {

        taskService.deleteTask(id, taskId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Mark a task as completed", description = "Mark a specific task as completed")
    public ResponseEntity<ApiResponseDTO<MaintenanceTaskDTO>> completeTask(
            @PathVariable Long id,
            @PathVariable Long taskId) {

        MaintenanceTaskDTO completed = taskService.completeTask(id, taskId);

        ApiResponseDTO<MaintenanceTaskDTO> response = new ApiResponseDTO<>(
                true,
                "Task completed successfully",
                completed,
                null);

        return ResponseEntity.ok(response);
    }
}
