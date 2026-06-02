package it.f3rren.aquarium.manual_parameters_service.controller;

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

import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.manual_parameters_service.service.IManualParameterService;

@WebMvcTest(ManualParameterController.class)
class ManualParameterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IManualParameterService manualParameterService;

    private ManualParameterDTO sampleParameter;

    @BeforeEach
    void setUp() {
        sampleParameter = new ManualParameterDTO();
        sampleParameter.setId(1L);
        sampleParameter.setAquariumId(1L);
        sampleParameter.setCalcium(420.0);
        sampleParameter.setMeasuredAt(LocalDateTime.of(2025, 1, 1, 12, 0));
    }

    @Nested
    class AddManualParameter {

        @Test
        void createsSuccessfully() throws Exception {
            CreateManualParameterDTO dto = new CreateManualParameterDTO();
            dto.setCalcium(420.0);

            when(manualParameterService.saveManualParameter(eq(1L), any(CreateManualParameterDTO.class)))
                    .thenReturn(sampleParameter);

            mockMvc.perform(post("/aquariums/1/parameters/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.calcium").value(420.0));

            verify(manualParameterService).saveManualParameter(eq(1L), any(CreateManualParameterDTO.class));
        }

        @Test
        void returns400WhenCalciumIsNegative() throws Exception {
            CreateManualParameterDTO dto = new CreateManualParameterDTO();
            dto.setCalcium(-1.0);

            mockMvc.perform(post("/aquariums/1/parameters/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class GetLatestManualParameter {

        @Test
        void returnsLatestSuccessfully() throws Exception {
            when(manualParameterService.getLatestManualParameter(1L)).thenReturn(sampleParameter);

            mockMvc.perform(get("/aquariums/1/parameters/manual"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.calcium").value(420.0));
        }

        @Test
        void returns404WhenNoneFound() throws Exception {
            when(manualParameterService.getLatestManualParameter(99L))
                    .thenThrow(new ResourceNotFoundException("No parameters found for aquarium 99"));

            mockMvc.perform(get("/aquariums/99/parameters/manual"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class GetManualParametersHistory {

        @Test
        void returnsFullHistoryWhenNoDates() throws Exception {
            when(manualParameterService.getAllManualParameters(1L)).thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters/manual/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void returnsFilteredHistoryWhenDatesProvided() throws Exception {
            when(manualParameterService.getManualParametersHistory(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters/manual/history")
                            .param("from", "2025-01-01T00:00:00")
                            .param("to", "2025-12-31T23:59:59"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void returnsAllHistoryWhenOnlyFromProvided() throws Exception {
            when(manualParameterService.getAllManualParameters(1L)).thenReturn(List.of(sampleParameter));

            mockMvc.perform(get("/aquariums/1/parameters/manual/history")
                            .param("from", "2025-01-01T00:00:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void returns400ForInvalidDateFormat() throws Exception {
            mockMvc.perform(get("/aquariums/1/parameters/manual/history")
                            .param("from", "not-a-date")
                            .param("to", "2025-12-31T23:59:59"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
