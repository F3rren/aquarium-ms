package it.f3rren.aquarium.aquariums_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.aquariums_service.client.ParametersClient;
import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.service.AquariumService;

/**
 * Integration tests for AquariumController.
 * Uses @WebMvcTest to test the controller layer with MockMvc.
 * Service and client dependencies are mocked.
 */
@WebMvcTest(AquariumController.class)
class AquariumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AquariumService aquariumService;

    @MockBean
    private ParametersClient parametersClient;

    private Aquarium sampleAquarium;

    @BeforeEach
    void setUp() {
        sampleAquarium = new Aquarium();
        sampleAquarium.setId(1L);
        sampleAquarium.setName("Reef Tank");
        sampleAquarium.setVolume(200);
        sampleAquarium.setType("saltwater");
        sampleAquarium.setDescription("A reef aquarium");
        sampleAquarium.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));
    }

    // ========================
    // GET /aquariums
    // ========================

    @Nested
    @DisplayName("GET /aquariums")
    class GetAllAquariums {

        @Test
        @DisplayName("should return paginated list of aquariums")
        void shouldReturnList() throws Exception {
            when(aquariumService.getAllAquariums(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(sampleAquarium)));

            mockMvc.perform(get("/aquariums"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].name").value("Reef Tank"))
                    .andExpect(jsonPath("$.data[0].volume").value(200))
                    .andExpect(jsonPath("$.metadata.totalElements").value(1));
        }

        @Test
        @DisplayName("should return empty list when no aquariums exist")
        void shouldReturnEmptyList() throws Exception {
            when(aquariumService.getAllAquariums(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get("/aquariums"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    // ========================
    // GET /aquariums/{id}
    // ========================

    @Nested
    @DisplayName("GET /aquariums/{id}")
    class GetAquariumById {

        @Test
        @DisplayName("should return aquarium when found")
        void shouldReturnAquarium() throws Exception {
            when(aquariumService.getAquariumById(1L)).thenReturn(sampleAquarium);

            mockMvc.perform(get("/aquariums/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Reef Tank"));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(aquariumService.getAquariumById(99L))
                    .thenThrow(new ResourceNotFoundException("Aquarium not found with ID: 99"));

            mockMvc.perform(get("/aquariums/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("should return 400 for invalid ID type")
        void shouldReturn400ForInvalidId() throws Exception {
            mockMvc.perform(get("/aquariums/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ========================
    // POST /aquariums
    // ========================

    @Nested
    @DisplayName("POST /aquariums")
    class CreateAquarium {

        @Test
        @DisplayName("should create aquarium and return 201")
        void shouldCreate() throws Exception {
            CreateAquariumDTO dto = new CreateAquariumDTO();
            dto.setName("New Tank");
            dto.setVolume(150);
            dto.setType("freshwater");

            when(aquariumService.createAquarium(any(CreateAquariumDTO.class))).thenReturn(sampleAquarium);

            mockMvc.perform(post("/aquariums")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Reef Tank"));
        }

        @Test
        @DisplayName("should return 400 for missing required fields")
        void shouldReturn400ForInvalidDto() throws Exception {
            CreateAquariumDTO dto = new CreateAquariumDTO();
            // name is blank — should fail @NotBlank validation

            mockMvc.perform(post("/aquariums")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("should return 400 for invalid type")
        void shouldReturn400ForInvalidType() throws Exception {
            CreateAquariumDTO dto = new CreateAquariumDTO();
            dto.setName("Tank");
            dto.setVolume(100);
            dto.setType("invalid-type");

            mockMvc.perform(post("/aquariums")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ========================
    // PUT /aquariums/{id}
    // ========================

    @Nested
    @DisplayName("PUT /aquariums/{id}")
    class UpdateAquarium {

        @Test
        @DisplayName("should update aquarium successfully")
        void shouldUpdate() throws Exception {
            UpdateAquariumDTO dto = new UpdateAquariumDTO();
            dto.setName("Updated Name");

            when(aquariumService.updateAquarium(eq(1L), any(UpdateAquariumDTO.class))).thenReturn(sampleAquarium);

            mockMvc.perform(put("/aquariums/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("should return 404 when aquarium not found")
        void shouldReturn404() throws Exception {
            UpdateAquariumDTO dto = new UpdateAquariumDTO();
            dto.setName("Updated");

            when(aquariumService.updateAquarium(eq(99L), any(UpdateAquariumDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Aquarium not found with ID: 99"));

            mockMvc.perform(put("/aquariums/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ========================
    // DELETE /aquariums/{id}
    // ========================

    @Nested
    @DisplayName("DELETE /aquariums/{id}")
    class DeleteAquarium {

        @Test
        @DisplayName("should delete aquarium and return 204 No Content")
        void shouldDelete() throws Exception {
            doNothing().when(aquariumService).deleteAquarium(1L);

            mockMvc.perform(delete("/aquariums/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when aquarium not found")
        void shouldReturn404() throws Exception {
            doThrow(new ResourceNotFoundException("Aquarium not found with ID: 99"))
                    .when(aquariumService).deleteAquarium(99L);

            mockMvc.perform(delete("/aquariums/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // ========================
    // Parameter validation
    // ========================

    @Nested
    @DisplayName("Parameter validation")
    class ParameterValidation {

        @Test
        @DisplayName("should return 400 when limit exceeds 100")
        void shouldReturn400ForLimitTooHigh() throws Exception {
            mockMvc.perform(get("/aquariums/1/water-parameters")
                            .param("limit", "999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("should return 400 for invalid period value")
        void shouldReturn400ForInvalidPeriod() throws Exception {
            mockMvc.perform(get("/aquariums/1/water-parameters/history")
                            .param("period", "year"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
