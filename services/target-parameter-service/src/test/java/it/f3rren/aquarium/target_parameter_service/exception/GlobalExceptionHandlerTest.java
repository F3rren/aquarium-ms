package it.f3rren.aquarium.target_parameter_service.exception;

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

import it.f3rren.aquarium.target_parameter_service.controller.TargetParameterController;
import it.f3rren.aquarium.target_parameter_service.service.ITargetParameterService;

/**
 * Covers the IllegalArgumentException handler not exercised by TargetParameterControllerTest.
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
                    .andExpect(jsonPath("$.message").value("invalid aquarium argument"));
        }
    }
}
