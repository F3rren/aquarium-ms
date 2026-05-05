package it.f3rren.aquarium.target_parameter_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.dto.TargetParameterResponseDTO;
import it.f3rren.aquarium.target_parameter_service.service.ITargetParameterService;

@WebMvcTest(TargetParameterController.class)
class TargetParameterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ITargetParameterService targetParameterService;

    private TargetParameterResponseDTO sampleTargets;

    @BeforeEach
    void setUp() {
        sampleTargets = new TargetParameterResponseDTO();
        sampleTargets.setAquariumId(1L);
        sampleTargets.setTemperature(26.0);
        sampleTargets.setPh(8.2);
        sampleTargets.setSalinity(35.0);
    }

    @Nested
    class GetTargetParameters {

        @Test
        void returnsTargetsWhenPresent() throws Exception {
            when(targetParameterService.getTargetParameters(1L)).thenReturn(sampleTargets);

            mockMvc.perform(get("/aquariums/1/settings/targets"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.temperature").value(26.0));
        }

        @Test
        void returnsNullDataWhenNoCustomTargets() throws Exception {
            when(targetParameterService.getTargetParameters(1L)).thenReturn(null);

            mockMvc.perform(get("/aquariums/1/settings/targets"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("No custom target parameter found"));
        }
    }

    @Nested
    class SaveTargetParameters {

        @Test
        void savesSuccessfully() throws Exception {
            SaveTargetParameterDTO dto = new SaveTargetParameterDTO();
            dto.setTemperature(26.0);
            dto.setPh(8.2);
            dto.setSalinity(35.0);
            dto.setOrp(350.0);

            when(targetParameterService.saveTargetParameters(eq(1L), any(SaveTargetParameterDTO.class)))
                    .thenReturn(sampleTargets);

            mockMvc.perform(post("/aquariums/1/settings/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.temperature").value(26.0));
        }

        @Test
        void savesWithAllNullFields() throws Exception {
            SaveTargetParameterDTO dto = new SaveTargetParameterDTO();
            // all fields optional — should succeed

            when(targetParameterService.saveTargetParameters(eq(1L), any(SaveTargetParameterDTO.class)))
                    .thenReturn(sampleTargets);

            mockMvc.perform(post("/aquariums/1/settings/targets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
