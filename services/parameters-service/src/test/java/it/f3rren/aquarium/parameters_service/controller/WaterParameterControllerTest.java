package it.f3rren.aquarium.parameters_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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

import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.dto.ParameterDTO;
import it.f3rren.aquarium.parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.parameters_service.service.IParameterService;

@WebMvcTest(WaterParameterController.class)
class WaterParameterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IParameterService parameterService;

    private ParameterDTO sampleParameter;

    @BeforeEach
    void setUp() {
        sampleParameter = new ParameterDTO();
        sampleParameter.setId(1L);
        sampleParameter.setAquariumId(1L);
        sampleParameter.setTemperature(25.5);
        sampleParameter.setPh(8.2);
        sampleParameter.setSalinity(35);
    }

    @Nested
    class AddParameter {

        @Test
        void createsSuccessfully() throws Exception {
            CreateParameterDTO dto = new CreateParameterDTO();
            dto.setTemperature(25.5);
            dto.setPh(8.2);
            dto.setSalinity(35);
            dto.setOrp(350);

            when(parameterService.saveParameter(eq(1L), any(CreateParameterDTO.class))).thenReturn(sampleParameter);

            mockMvc.perform(post("/aquariums/1/parameters")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.temperature").value(25.5));
        }

        @Test
        void returns400WhenRequiredFieldsMissing() throws Exception {
            CreateParameterDTO dto = new CreateParameterDTO();

            mockMvc.perform(post("/aquariums/1/parameters")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class GetParametersByAquarium {

        @Test
        void returnsParametersWithDefaultLimit() throws Exception {
            when(parameterService.getParametersByAquariumId(1L, 10)).thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void returnsParametersWithCustomLimit() throws Exception {
            when(parameterService.getParametersByAquariumId(1L, 5)).thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters").param("limit", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    class GetLatestParameter {

        @Test
        void returnsLatestSuccessfully() throws Exception {
            when(parameterService.getLatestParameter(1L)).thenReturn(sampleParameter);

            mockMvc.perform(get("/aquariums/1/parameters/latest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.ph").value(8.2));
        }

        @Test
        void returns404WhenNoneFound() throws Exception {
            when(parameterService.getLatestParameter(99L))
                    .thenThrow(new ResourceNotFoundException("No parameters found for aquarium 99"));

            mockMvc.perform(get("/aquariums/99/parameters/latest"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class GetParametersHistory {

        @Test
        void returnsHistoryByPeriod() throws Exception {
            when(parameterService.getParametersByPeriod(1L, "week")).thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters/history").param("period", "week"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void returnsHistoryByDateRange() throws Exception {
            when(parameterService.getParametersHistory(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters/history")
                            .param("from", "2025-01-01T00:00:00")
                            .param("to", "2025-12-31T23:59:59"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void returnsDefaultWeekWhenNeitherPeriodNorDatesProvided() throws Exception {
            when(parameterService.getParametersByPeriod(1L, "week")).thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void returnsAllHistoryWhenOnlyFromProvided() throws Exception {
            when(parameterService.getParametersByPeriod(1L, "week")).thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters/history")
                            .param("from", "2025-01-01T00:00:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
