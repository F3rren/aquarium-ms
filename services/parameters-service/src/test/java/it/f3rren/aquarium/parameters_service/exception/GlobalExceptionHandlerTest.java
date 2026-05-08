package it.f3rren.aquarium.parameters_service.exception;

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

import it.f3rren.aquarium.parameters_service.controller.WaterParameterController;
import it.f3rren.aquarium.parameters_service.service.IParameterService;

/**
 * Covers exception-handler branches not exercised by WaterParameterControllerTest.
 */
@WebMvcTest(WaterParameterController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IParameterService parameterService;

    @Nested
    @DisplayName("IllegalArgumentException → 400")
    class IllegalArgumentTests {

        @Test
        @DisplayName("should return 400 when service throws IllegalArgumentException")
        void shouldReturn400ForIllegalArgument() throws Exception {
            when(parameterService.getLatestParameter(anyLong()))
                    .thenThrow(new IllegalArgumentException("invalid argument"));

            mockMvc.perform(get("/aquariums/1/parameters/latest"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("invalid argument"));
        }
    }

    @Nested
    @DisplayName("DateTimeParseException → 400")
    class DateTimeParseTests {

        @Test
        @DisplayName("should return 400 when date format is invalid in history endpoint")
        void shouldReturn400ForInvalidDateFormat() throws Exception {
            mockMvc.perform(get("/aquariums/1/parameters/history")
                            .param("from", "not-a-date")
                            .param("to", "2025-12-31T23:59:59"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException → 400")
    class TypeMismatchTests {

        @Test
        @DisplayName("should return 400 for non-numeric aquarium ID in path")
        void shouldReturn400ForInvalidIdType() throws Exception {
            mockMvc.perform(get("/aquariums/abc/parameters/latest"))
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
            when(parameterService.getLatestParameter(anyLong()))
                    .thenThrow(new RuntimeException("unexpected error"));

            mockMvc.perform(get("/aquariums/1/parameters/latest"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("An internal error occurred"));
        }
    }
}
