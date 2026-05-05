package it.f3rren.aquarium.maintenance_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.maintenance_service.dto.request.CreateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.UpdateMaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.dto.response.MaintenanceTaskDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.service.IMaintenanceTaskService;

@WebMvcTest(MaintenanceTaskController.class)
class MaintenanceTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMaintenanceTaskService taskService;

    private MaintenanceTaskDTO sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new MaintenanceTaskDTO();
        sampleTask.setId(1L);
        sampleTask.setAquariumId(1L);
        sampleTask.setTitle("Water change");
        sampleTask.setIsCompleted(false);
    }

    @Nested
    class GetAllTasks {

        @Test
        void returnsAllTasks() throws Exception {
            when(taskService.getAllTasks(1L)).thenReturn(List.of(sampleTask));

            mockMvc.perform(get("/aquariums/1/tasks"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].title").value("Water change"));
        }

        @Test
        void returnsPendingTasksWhenStatusIsPending() throws Exception {
            when(taskService.getPendingTasks(1L)).thenReturn(List.of(sampleTask));

            mockMvc.perform(get("/aquariums/1/tasks").param("status", "pending"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void returnsCompletedTasksWhenStatusIsCompleted() throws Exception {
            when(taskService.getTasksByStatus(1L, true)).thenReturn(List.of());

            mockMvc.perform(get("/aquariums/1/tasks").param("status", "completed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    class CreateTask {

        @Test
        void createsSuccessfully() throws Exception {
            CreateMaintenanceTaskDTO dto = new CreateMaintenanceTaskDTO();
            dto.setTitle("Filter cleaning");

            when(taskService.createTask(eq(1L), any(CreateMaintenanceTaskDTO.class))).thenReturn(sampleTask);

            mockMvc.perform(post("/aquariums/1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.title").value("Water change"));
        }

        @Test
        void returns400WhenTitleIsBlank() throws Exception {
            CreateMaintenanceTaskDTO dto = new CreateMaintenanceTaskDTO();
            // title is null → @NotBlank fails

            mockMvc.perform(post("/aquariums/1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class UpdateTask {

        @Test
        void updatesSuccessfully() throws Exception {
            UpdateMaintenanceTaskDTO dto = new UpdateMaintenanceTaskDTO();
            dto.setTitle("Updated title");

            when(taskService.updateTask(eq(1L), eq(1L), any(UpdateMaintenanceTaskDTO.class))).thenReturn(sampleTask);

            mockMvc.perform(put("/aquariums/1/tasks/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            UpdateMaintenanceTaskDTO dto = new UpdateMaintenanceTaskDTO();
            dto.setTitle("Updated");

            when(taskService.updateTask(eq(1L), eq(99L), any()))
                    .thenThrow(new ResourceNotFoundException("Task not found with ID: 99"));

            mockMvc.perform(put("/aquariums/1/tasks/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void returns400WhenAquariumMismatch() throws Exception {
            UpdateMaintenanceTaskDTO dto = new UpdateMaintenanceTaskDTO();
            dto.setTitle("Updated");

            when(taskService.updateTask(eq(1L), eq(2L), any()))
                    .thenThrow(new IllegalArgumentException("Task does not belong to aquarium 1"));

            mockMvc.perform(put("/aquariums/1/tasks/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class DeleteTask {

        @Test
        void deletesSuccessfully() throws Exception {
            doNothing().when(taskService).deleteTask(1L, 1L);

            mockMvc.perform(delete("/aquariums/1/tasks/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Task not found with ID: 99"))
                    .when(taskService).deleteTask(1L, 99L);

            mockMvc.perform(delete("/aquariums/1/tasks/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class CompleteTask {

        @Test
        void completesSuccessfully() throws Exception {
            MaintenanceTaskDTO completed = new MaintenanceTaskDTO();
            completed.setId(1L);
            completed.setIsCompleted(true);

            when(taskService.completeTask(1L, 1L)).thenReturn(completed);

            mockMvc.perform(post("/aquariums/1/tasks/1/complete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.isCompleted").value(true));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            when(taskService.completeTask(1L, 99L))
                    .thenThrow(new ResourceNotFoundException("Task not found with ID: 99"));

            mockMvc.perform(post("/aquariums/1/tasks/99/complete"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
