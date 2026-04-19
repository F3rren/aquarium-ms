package it.f3rren.aquarium.species.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.species.dto.CoralResponseDTO;
import it.f3rren.aquarium.species.dto.FishResponseDTO;
import it.f3rren.aquarium.species.exception.ResourceNotFoundException;
import it.f3rren.aquarium.species.model.Coral;
import it.f3rren.aquarium.species.model.Fish;
import it.f3rren.aquarium.species.repository.ICoralRepository;
import it.f3rren.aquarium.species.repository.IFishRepository;

@ExtendWith(MockitoExtension.class)
class SpeciesServiceTest {

    @Mock
    private IFishRepository fishRepository;

    @Mock
    private ICoralRepository coralRepository;

    @InjectMocks
    private SpeciesService speciesService;

    private final Fish sampleFish = new Fish(1L, "Clownfish", "Amphiprioninae", "Pomacentridae", 50, 11, "Easy", true, "Peaceful", "Omnivore", null, null, "Marine");
    private final Coral sampleCoral = new Coral(1L, "Hammer Coral", "Euphyllia ancora", "LPS", 100, 30, "Medium", "Medium", "Medium", "Middle", false, "Meaty foods", "A popular LPS coral");

    @Nested
    class GetAllFish {

        @Test
        void returnsDtoList() {
            Fish f2 = new Fish(2L, "Tang", "Acanthuridae", "Acanthuridae", 200, 40, "Medium", true, "Semi-aggressive", "Herbivore", null, null, "Marine");
            when(fishRepository.findAllSortedByName()).thenReturn(List.of(sampleFish, f2));

            List<FishResponseDTO> result = speciesService.getAllFish();

            assertEquals(2, result.size());
            assertEquals("Clownfish", result.get(0).getCommonName());
            verify(fishRepository).findAllSortedByName();
        }
    }

    @Nested
    class GetFishById {

        @Test
        void returnsDto() {
            when(fishRepository.findById(1L)).thenReturn(Optional.of(sampleFish));

            FishResponseDTO result = speciesService.getFishById(1L);

            assertEquals("Clownfish", result.getCommonName());
            assertEquals("Marine", result.getWaterType());
        }

        @Test
        void throwsWhenNotFound() {
            when(fishRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> speciesService.getFishById(99L));
        }
    }

    @Nested
    class GetAllCorals {

        @Test
        void returnsDtoList() {
            Coral c2 = new Coral(2L, "Torch Coral", "Euphyllia glabrescens", "LPS", 100, 25, "Medium", "Low", "Low", "Bottom", false, "Meaty foods", "A flowing LPS coral");
            when(coralRepository.findAllSortedByName()).thenReturn(List.of(sampleCoral, c2));

            List<CoralResponseDTO> result = speciesService.getAllCorals();

            assertEquals(2, result.size());
            assertEquals("Hammer Coral", result.get(0).getCommonName());
            verify(coralRepository).findAllSortedByName();
        }
    }

    @Nested
    class GetCoralById {

        @Test
        void returnsDto() {
            when(coralRepository.findById(1L)).thenReturn(Optional.of(sampleCoral));

            CoralResponseDTO result = speciesService.getCoralById(1L);

            assertEquals("Hammer Coral", result.getCommonName());
            assertEquals("LPS", result.getType());
        }

        @Test
        void throwsWhenNotFound() {
            when(coralRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> speciesService.getCoralById(99L));
        }
    }
}
