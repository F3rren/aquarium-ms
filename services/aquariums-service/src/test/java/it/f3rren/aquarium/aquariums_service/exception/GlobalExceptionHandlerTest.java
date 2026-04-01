package it.f3rren.aquarium.aquariums_service.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import it.f3rren.aquarium.aquariums_service.client.ParametersClient;
import it.f3rren.aquarium.aquariums_service.controller.AquariumController;
import it.f3rren.aquarium.aquariums_service.service.AquariumService;

/**
 * Tests for GlobalExceptionHandler, covering exception types not exercised elsewhere.
 */
@WebMvcTest(AquariumController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AquariumService aquariumService;

    @MockBean
    private ParametersClient parametersClient;

    @Nested
    @DisplayName("DataIntegrityViolationException")
    class DataIntegrityTests {

        @Test
        @DisplayName("should return 409 Conflict on DB constraint violation")
        void shouldReturn409OnConstraintViolation() throws Exception {
            when(aquariumService.getAllAquariums(any(Pageable.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

            mockMvc.perform(get("/aquariums"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Data integrity constraint violated"));
        }
    }

    @Nested
    @DisplayName("RestClientException")
    class RestClientTests {

        @Test
        @DisplayName("should return 503 when inter-service call fails")
        void shouldReturn503OnRestClientFailure() throws Exception {
            when(aquariumService.getAquariumById(anyLong()))
                    .thenThrow(new ResourceAccessException("Connection refused"));

            mockMvc.perform(get("/aquariums/1"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("External service communication error"));
        }
    }

    @Nested
    @DisplayName("ResourceNotFoundException")
    class ResourceNotFoundTests {

        @Test
        @DisplayName("should return 404 with error message")
        void shouldReturn404() throws Exception {
            when(aquariumService.getAquariumById(99L))
                    .thenThrow(new ResourceNotFoundException("Aquarium not found with ID: 99"));

            mockMvc.perform(get("/aquariums/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Aquarium not found with ID: 99"));
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException")
    class IllegalArgumentTests {

        @Test
        @DisplayName("should return 400 when limit is out of bounds")
        void shouldReturn400ForInvalidLimit() throws Exception {
            mockMvc.perform(get("/aquariums/1/water-parameters")
                            .param("limit", "999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
