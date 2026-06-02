package it.f3rren.aquarium.inhabitants_service.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import it.f3rren.aquarium.inhabitants_service.controller.InhabitantController;
import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.inhabitants_service.model.InhabitantType;
import it.f3rren.aquarium.inhabitants_service.service.IInhabitantService;

/**
 * Covers exception-handler branches not exercised by InhabitantControllerTest.
 */
@WebMvcTest(InhabitantController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IInhabitantService inhabitantService;

    @Nested
    @DisplayName("Generic Exception → 500")
    class GenericExceptionTests {

        @Test
        @DisplayName("should return 500 for unexpected RuntimeException from service")
        void shouldReturn500ForUnexpectedException() throws Exception {
            when(inhabitantService.getInhabitantsByAquariumId(anyLong()))
                    .thenThrow(new RuntimeException("unexpected error"));

            mockMvc.perform(get("/aquariums/1/inhabitants"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
        }
    }

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException → 400")
    class TypeMismatchTests {

        @Test
        @DisplayName("should return 400 for non-numeric aquariumId in path")
        void shouldReturn400ForInvalidAquariumIdType() throws Exception {
            mockMvc.perform(get("/aquariums/abc/inhabitants"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("ResourceNotFoundException → 404")
    class ResourceNotFoundTests {

        @Test
        @DisplayName("should return 404 with error message")
        void shouldReturn404() throws Exception {
            when(inhabitantService.getInhabitantsByAquariumId(99L))
                    .thenThrow(new ResourceNotFoundException("Aquarium not found with ID: 99"));

            mockMvc.perform(get("/aquariums/99/inhabitants"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Aquarium not found with ID: 99"));
        }
    }

    @Nested
    @DisplayName("HttpMessageNotReadableException → 400")
    class HttpMessageNotReadableTests {

        @Test
        @DisplayName("should return 400 for unrecognized enum value in request body")
        void shouldReturn400ForInvalidEnumValue() throws Exception {
            mockMvc.perform(post("/aquariums/1/inhabitants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"inhabitantType\": \"INVALID\", \"inhabitantId\": 1}"))
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
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            dto.setInhabitantType(InhabitantType.FISH);
            dto.setInhabitantId(1L);

            when(inhabitantService.addInhabitant(anyLong(), any(CreateInhabitantDTO.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

            mockMvc.perform(post("/aquariums/1/inhabitants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Data integrity constraint violated"));
        }
    }
}
