package it.f3rren.aquarium.manual_parameters_service.exception;

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
import org.springframework.test.web.servlet.MockMvc;

import it.f3rren.aquarium.manual_parameters_service.controller.ManualParameterController;
import it.f3rren.aquarium.manual_parameters_service.service.IManualParameterService;

/**
 * Covers exception-handler branches not exercised by ManualParameterControllerTest.
 */
@WebMvcTest(ManualParameterController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

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
                    .andExpect(jsonPath("$.message").value("invalid argument"));
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
}
