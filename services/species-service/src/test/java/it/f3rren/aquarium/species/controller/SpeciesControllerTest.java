package it.f3rren.aquarium.species.controller;

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
import org.springframework.test.web.servlet.MockMvc;

import it.f3rren.aquarium.species.dto.CoralResponseDTO;
import it.f3rren.aquarium.species.dto.FishResponseDTO;
import it.f3rren.aquarium.species.exception.ResourceNotFoundException;
import it.f3rren.aquarium.species.service.ISpeciesService;

@WebMvcTest(SpeciesController.class)
class SpeciesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISpeciesService speciesService;

    private FishResponseDTO sampleFish;
    private CoralResponseDTO sampleCoral;

    @BeforeEach
    void setUp() {
        sampleFish = new FishResponseDTO();
        sampleFish.setId(1L);
        sampleFish.setCommonName("Clownfish");
        sampleFish.setScientificName("Amphiprion ocellaris");

        sampleCoral = new CoralResponseDTO();
        sampleCoral.setId(1L);
        sampleCoral.setCommonName("Hammer Coral");
        sampleCoral.setScientificName("Euphyllia ancora");
    }

    @Nested
    class GetAllFish {

        @Test
        void returnsListSuccessfully() throws Exception {
            when(speciesService.getAllFish()).thenReturn(List.of(sampleFish));

            mockMvc.perform(get("/species/fish"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].commonName").value("Clownfish"));
        }

        @Test
        void returnsEmptyList() throws Exception {
            when(speciesService.getAllFish()).thenReturn(List.of());

            mockMvc.perform(get("/species/fish"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    class GetFishById {

        @Test
        void returnsWhenFound() throws Exception {
            when(speciesService.getFishById(1L)).thenReturn(sampleFish);

            mockMvc.perform(get("/species/fish/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.commonName").value("Clownfish"));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            when(speciesService.getFishById(99L))
                    .thenThrow(new ResourceNotFoundException("Fish not found with ID: 99"));

            mockMvc.perform(get("/species/fish/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class GetAllCorals {

        @Test
        void returnsListSuccessfully() throws Exception {
            when(speciesService.getAllCorals()).thenReturn(List.of(sampleCoral));

            mockMvc.perform(get("/species/corals"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].commonName").value("Hammer Coral"));
        }

        @Test
        void returnsEmptyList() throws Exception {
            when(speciesService.getAllCorals()).thenReturn(List.of());

            mockMvc.perform(get("/species/corals"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    class GetCoralById {

        @Test
        void returnsWhenFound() throws Exception {
            when(speciesService.getCoralById(1L)).thenReturn(sampleCoral);

            mockMvc.perform(get("/species/corals/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.commonName").value("Hammer Coral"));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            when(speciesService.getCoralById(99L))
                    .thenThrow(new ResourceNotFoundException("Coral not found with ID: 99"));

            mockMvc.perform(get("/species/corals/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
