package it.f3rren.aquarium.inhabitants_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.FishDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.inhabitants_service.model.Inhabitant;
import it.f3rren.aquarium.inhabitants_service.model.InhabitantType;
import it.f3rren.aquarium.inhabitants_service.repository.IInhabitantRepository;

@ExtendWith(MockitoExtension.class)
class InhabitantServiceTest {

    @Mock
    private IInhabitantRepository inhabitantRepository;

    @Mock
    private FishService fishService;

    @Mock
    private CoralService coralService;

    @InjectMocks
    private InhabitantService inhabitantService;

    private Inhabitant sampleFishInhabitant;
    private FishDTO sampleFish;

    @BeforeEach
    void setUp() {
        sampleFishInhabitant = new Inhabitant();
        sampleFishInhabitant.setId(1L);
        sampleFishInhabitant.setAquariumId(10L);
        sampleFishInhabitant.setInhabitantType("fish");
        sampleFishInhabitant.setInhabitantId(100L);
        sampleFishInhabitant.setQuantity(2);
        sampleFishInhabitant.setAddedDate(LocalDateTime.now());

        sampleFish = new FishDTO();
        sampleFish.setId(100L);
        sampleFish.setCommonName("Clownfish");
        sampleFish.setScientificName("Amphiprioninae");
    }

    @Nested
    class GetInhabitantsByAquariumId {

        @Test
        void returnsEnrichedDtoList() {
            when(inhabitantRepository.findByAquariumId(10L)).thenReturn(List.of(sampleFishInhabitant));
            when(fishService.getFishById(100L)).thenReturn(sampleFish);

            List<InhabitantDetailsDTO> result = inhabitantService.getInhabitantsByAquariumId(10L);

            assertEquals(1, result.size());
            assertEquals("Clownfish", result.get(0).getCommonName());
            assertEquals("fish", result.get(0).getType());
            verify(inhabitantRepository).findByAquariumId(10L);
        }

        @Test
        void returnsEmptyListWhenNoInhabitants() {
            when(inhabitantRepository.findByAquariumId(99L)).thenReturn(List.of());

            List<InhabitantDetailsDTO> result = inhabitantService.getInhabitantsByAquariumId(99L);

            assertTrue(result.isEmpty());
        }

        @Test
        void usesCustomNameOverSpeciesName() {
            sampleFishInhabitant.setCustomName("Nemo");
            when(inhabitantRepository.findByAquariumId(10L)).thenReturn(List.of(sampleFishInhabitant));
            when(fishService.getFishById(100L)).thenReturn(sampleFish);

            List<InhabitantDetailsDTO> result = inhabitantService.getInhabitantsByAquariumId(10L);

            assertEquals("Nemo", result.get(0).getCommonName());
        }
    }

    @Nested
    class AddInhabitant {

        @Test
        void savesAndReturnsInhabitant() {
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            dto.setInhabitantType(InhabitantType.FISH);
            dto.setInhabitantId(100L);
            dto.setQuantity(1);

            when(fishService.getFishById(100L)).thenReturn(sampleFish);
            when(inhabitantRepository.save(any(Inhabitant.class))).thenAnswer(i -> i.getArgument(0));

            Inhabitant result = inhabitantService.addInhabitant(10L, dto);

            assertEquals(10L, result.getAquariumId());
            assertEquals("fish", result.getInhabitantType());
            assertEquals(100L, result.getInhabitantId());
            verify(inhabitantRepository).save(any(Inhabitant.class));
        }

        @Test
        void throwsWhenSpeciesNotFound() {
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            dto.setInhabitantType(InhabitantType.FISH);
            dto.setInhabitantId(999L);

            when(fishService.getFishById(999L)).thenThrow(new ResourceNotFoundException("Fish not found"));

            assertThrows(ResourceNotFoundException.class,
                    () -> inhabitantService.addInhabitant(10L, dto));
            verify(inhabitantRepository, never()).save(any());
        }
    }

    @Nested
    class RemoveInhabitant {

        @Test
        void deletesExistingInhabitant() {
            when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(sampleFishInhabitant));

            inhabitantService.removeInhabitant(10L, 1L);

            verify(inhabitantRepository).deleteById(1L);
        }

        @Test
        void throwsWhenInhabitantNotFound() {
            when(inhabitantRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> inhabitantService.removeInhabitant(10L, 99L));
            verify(inhabitantRepository, never()).deleteById(any());
        }

        @Test
        void throwsWhenInhabitantBelongsToDifferentAquarium() {
            when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(sampleFishInhabitant));

            assertThrows(ResourceNotFoundException.class,
                    () -> inhabitantService.removeInhabitant(99L, 1L));
            verify(inhabitantRepository, never()).deleteById(any());
        }
    }

    @Nested
    class UpdateInhabitant {

        @Test
        void updatesOnlyNonNullFields() {
            when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(sampleFishInhabitant));
            when(inhabitantRepository.save(any(Inhabitant.class))).thenAnswer(i -> i.getArgument(0));

            UpdateInhabitantDTO dto = new UpdateInhabitantDTO();
            dto.setQuantity(5);
            dto.setNotes("Updated notes");

            Inhabitant result = inhabitantService.updateInhabitant(10L, 1L, dto);

            assertEquals(5, result.getQuantity());
            assertEquals("Updated notes", result.getNotes());
            assertNull(result.getCustomName());
        }

        @Test
        void throwsWhenInhabitantNotFound() {
            when(inhabitantRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> inhabitantService.updateInhabitant(10L, 99L, new UpdateInhabitantDTO()));
        }
    }
}
