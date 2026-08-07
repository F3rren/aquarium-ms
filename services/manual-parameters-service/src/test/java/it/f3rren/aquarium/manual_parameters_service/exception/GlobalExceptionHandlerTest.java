package it.f3rren.aquarium.manual_parameters_service.exception;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.manual_parameters_service.controller.ManualParameterController;
import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.manual_parameters_service.service.IManualParameterService;

/**
 * Covers exception-handler branches not exercised by ManualParameterControllerTest.
 */
@WebMvcTest(ManualParameterController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IManualParameterService manualParameterService;

    @Nested
    @DisplayName("IllegalArgumentException → 400")
    class IllegalArgumentTests {

        @Test
        @DisplayName("should return 400 when service throws IllegalArgumentException")
        void shouldReturn400ForIllegalArgument() throws Exception {
            when(manualParameterService.getLatestManualParameter(anyLong()))
                    .thenThrow(new IllegalArgumentException("invalid argument"));

            mockMvc.perform(get("/aquariums/1/parameters/manual"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Invalid argument"));
        }
    }

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException → 400")
    class TypeMismatchTests {

        @Test
        @DisplayName("should return 400 for non-numeric aquariumId in path")
        void shouldReturn400ForInvalidAquariumIdType() throws Exception {
            mockMvc.perform(get("/aquariums/abc/parameters/manual"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Generic Exception → 500")
    class GenericExceptionTests {

        @Test
        @DisplayName("should return 500 for unexpected RuntimeException from service")
        void shouldReturn500ForUnexpectedException() throws Exception {
            when(manualParameterService.getLatestManualParameter(anyLong()))
                    .thenThrow(new RuntimeException("unexpected error"));

            mockMvc.perform(get("/aquariums/1/parameters/manual"))
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
            when(manualParameterService.getLatestManualParameter(eq(99L)))
                    .thenThrow(new ResourceNotFoundException("No parameters found for aquarium 99"));

            mockMvc.perform(get("/aquariums/99/parameters/manual"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("No parameters found for aquarium 99"));
        }
    }

    @Nested
    @DisplayName("HttpMessageNotReadableException → 400")
    class HttpMessageNotReadableTests {

        @Test
        @DisplayName("should return 400 for non-numeric value in numeric field")
        void shouldReturn400ForMalformedBody() throws Exception {
            mockMvc.perform(post("/aquariums/1/parameters/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"calcium\":\"not-a-number\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Malformed or unreadable request body"));
        }
    }

    @Nested
    @DisplayName("DataIntegrityViolationException → 409")
    class DataIntegrityTests {

        @Test
        @DisplayName("should return 409 Conflict on DB constraint violation")
        void shouldReturn409OnConstraintViolation() throws Exception {
            CreateManualParameterDTO dto = new CreateManualParameterDTO();
            dto.setCalcium(420.0);

            when(manualParameterService.saveManualParameter(any(), any(CreateManualParameterDTO.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

            mockMvc.perform(post("/aquariums/1/parameters/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Data integrity constraint violated"));
        }
    }
}
