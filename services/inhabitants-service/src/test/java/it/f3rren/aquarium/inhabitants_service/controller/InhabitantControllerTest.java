package it.f3rren.aquarium.inhabitants_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.inhabitants_service.model.InhabitantType;
import it.f3rren.aquarium.inhabitants_service.service.IInhabitantService;

@WebMvcTest(InhabitantController.class)
class InhabitantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IInhabitantService inhabitantService;

    private InhabitantDetailsDTO sampleInhabitant;

    @BeforeEach
    void setUp() {
        sampleInhabitant = new InhabitantDetailsDTO();
        sampleInhabitant.setId(1L);
        sampleInhabitant.setType("fish");
        sampleInhabitant.setCommonName("Clownfish");
        sampleInhabitant.setQuantity(2);
    }

    @Nested
    class GetAquariumInhabitants {

        @Test
        void returnsListSuccessfully() throws Exception {
            when(inhabitantService.getInhabitantsByAquariumId(1L)).thenReturn(List.of(sampleInhabitant));

            mockMvc.perform(get("/aquariums/1/inhabitants"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].type").value("fish"));
        }

        @Test
        void returnsEmptyList() throws Exception {
            when(inhabitantService.getInhabitantsByAquariumId(1L)).thenReturn(List.of());

            mockMvc.perform(get("/aquariums/1/inhabitants"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        void returns400ForNonNumericAquariumId() throws Exception {
            mockMvc.perform(get("/aquariums/abc/inhabitants"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class AddInhabitant {

        @Test
        void createsSuccessfully() throws Exception {
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            dto.setInhabitantType(InhabitantType.FISH);
            dto.setInhabitantId(10L);

            when(inhabitantService.addInhabitant(eq(1L), any(CreateInhabitantDTO.class))).thenReturn(sampleInhabitant);

            mockMvc.perform(post("/aquariums/1/inhabitants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.type").value("fish"));
        }

        @Test
        void returns400WhenBodyIsInvalid() throws Exception {
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            // inhabitantType and inhabitantId null → @NotNull fails

            mockMvc.perform(post("/aquariums/1/inhabitants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void returns404WhenAquariumNotFound() throws Exception {
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            dto.setInhabitantType(InhabitantType.FISH);
            dto.setInhabitantId(10L);

            when(inhabitantService.addInhabitant(eq(99L), any()))
                    .thenThrow(new ResourceNotFoundException("Aquarium not found with ID: 99"));

            mockMvc.perform(post("/aquariums/99/inhabitants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class UpdateInhabitant {

        @Test
        void updatesSuccessfully() throws Exception {
            UpdateInhabitantDTO dto = new UpdateInhabitantDTO();
            dto.setQuantity(3);

            when(inhabitantService.updateInhabitant(eq(1L), eq(1L), any(UpdateInhabitantDTO.class)))
                    .thenReturn(sampleInhabitant);

            mockMvc.perform(put("/aquariums/1/inhabitants/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            UpdateInhabitantDTO dto = new UpdateInhabitantDTO();
            dto.setQuantity(3);

            when(inhabitantService.updateInhabitant(eq(1L), eq(99L), any()))
                    .thenThrow(new ResourceNotFoundException("Inhabitant not found with ID: 99"));

            mockMvc.perform(put("/aquariums/1/inhabitants/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void returns400WhenAquariumMismatch() throws Exception {
            UpdateInhabitantDTO dto = new UpdateInhabitantDTO();
            dto.setQuantity(3);

            when(inhabitantService.updateInhabitant(eq(1L), eq(2L), any()))
                    .thenThrow(new IllegalArgumentException("Inhabitant does not belong to aquarium"));

            mockMvc.perform(put("/aquariums/1/inhabitants/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class RemoveInhabitant {

        @Test
        void deletesSuccessfully() throws Exception {
            doNothing().when(inhabitantService).removeInhabitant(1L, 1L);

            mockMvc.perform(delete("/aquariums/1/inhabitants/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Inhabitant not found with ID: 99"))
                    .when(inhabitantService).removeInhabitant(1L, 99L);

            mockMvc.perform(delete("/aquariums/1/inhabitants/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
