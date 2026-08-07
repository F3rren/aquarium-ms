package it.f3rren.aquarium.target_parameter_service.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import it.f3rren.aquarium.target_parameter_service.controller.TargetParameterController;
import it.f3rren.aquarium.target_parameter_service.service.ITargetParameterService;

/**
 * Covers exception-handler branches not exercised by TargetParameterControllerTest.
 */
@WebMvcTest(TargetParameterController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITargetParameterService targetParameterService;

    @Nested
    @DisplayName("IllegalArgumentException → 400")
    class IllegalArgumentTests {

        @Test
        @DisplayName("should return 400 when service throws IllegalArgumentException")
        void shouldReturn400ForIllegalArgument() throws Exception {
            when(targetParameterService.getTargetParameters(anyLong()))
                    .thenThrow(new IllegalArgumentException("invalid aquarium argument"));

            mockMvc.perform(get("/aquariums/1/settings/targets"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Invalid argument"));
        }
    }

    @Nested
    @DisplayName("Generic Exception → 500")
    class GenericExceptionTests {

        @Test
        @DisplayName("should return 500 for unexpected RuntimeException from service")
        void shouldReturn500ForUnexpectedException() throws Exception {
            when(targetParameterService.getTargetParameters(anyLong()))
                    .thenThrow(new RuntimeException("unexpected error"));

            mockMvc.perform(get("/aquariums/1/settings/targets"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("An internal error occurred"));
        }
    }

    @Nested
    @DisplayName("ResourceNotFoundException → 404")
    class ResourceNotFoundTests {

        @Test
        @DisplayName("should return 404 with error message")
        void shouldReturn404() throws Exception {
            when(targetParameterService.saveTargetParameters(eq(99L), any()))
                    .thenThrow(new ResourceNotFoundException("Aquarium not found with ID: 99"));

            mockMvc.perform(post("/aquariums/99/settings/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"temperature\":25.0}"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Aquarium not found with ID: 99"));
        }
    }

    @Nested
    @DisplayName("HttpMessageNotReadable → 400")
    class HttpMessageNotReadableTests {

        @Test
        @DisplayName("should return 400 for malformed JSON body")
        void shouldReturn400ForMalformedJson() throws Exception {
            mockMvc.perform(post("/aquariums/1/settings/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"temperature\":\"not-a-number\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("DataIntegrityViolationException → 409")
    class DataIntegrityTests {

        @Test
        @DisplayName("should return 409 for constraint violation")
        void shouldReturn409ForDataIntegrity() throws Exception {
            when(targetParameterService.saveTargetParameters(eq(1L), any()))
                    .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

            mockMvc.perform(post("/aquariums/1/settings/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"temperature\":25.0}"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Data integrity violation"));
        }
    }
}
