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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.inhabitants_service.dto.CoralDTO;
import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.FishDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.inhabitants_service.model.Inhabitant;
import it.f3rren.aquarium.inhabitants_service.model.InhabitantType;
import it.f3rren.aquarium.inhabitants_service.client.SpeciesClient;
import it.f3rren.aquarium.inhabitants_service.mapper.InhabitantMapper;
import it.f3rren.aquarium.inhabitants_service.repository.IInhabitantRepository;

@ExtendWith(MockitoExtension.class)
class InhabitantServiceTest {

    @Mock
    private IInhabitantRepository inhabitantRepository;

    @Mock
    private SpeciesClient speciesClient;

    @Spy
    private InhabitantMapper inhabitantMapper = new InhabitantMapper();

    @InjectMocks
    private InhabitantService inhabitantService;

    private Inhabitant sampleFishInhabitant;
    private Inhabitant sampleCoralInhabitant;
    private FishDTO sampleFish;
    private CoralDTO sampleCoral;

    @BeforeEach
    void setUp() {
        sampleFishInhabitant = new Inhabitant();
        sampleFishInhabitant.setId(1L);
        sampleFishInhabitant.setAquariumId(10L);
        sampleFishInhabitant.setInhabitantType("fish");
        sampleFishInhabitant.setInhabitantId(100L);
        sampleFishInhabitant.setQuantity(2);
        sampleFishInhabitant.setAddedDate(LocalDateTime.now());

        sampleCoralInhabitant = new Inhabitant();
        sampleCoralInhabitant.setId(2L);
        sampleCoralInhabitant.setAquariumId(10L);
        sampleCoralInhabitant.setInhabitantType("coral");
        sampleCoralInhabitant.setInhabitantId(200L);
        sampleCoralInhabitant.setQuantity(1);
        sampleCoralInhabitant.setAddedDate(LocalDateTime.now());

        sampleFish = new FishDTO();
        sampleFish.setId(100L);
        sampleFish.setCommonName("Clownfish");
        sampleFish.setScientificName("Amphiprioninae");

        sampleCoral = new CoralDTO();
        sampleCoral.setId(200L);
        sampleCoral.setCommonName("Hammer Coral");
        sampleCoral.setScientificName("Euphyllia ancora");
    }

    @Nested
    class GetInhabitantsByAquariumId {

        @Test
        void returnsEnrichedDtoList() {
            when(inhabitantRepository.findByAquariumId(10L)).thenReturn(List.of(sampleFishInhabitant));
            when(speciesClient.getFishById(100L)).thenReturn(sampleFish);

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
            when(speciesClient.getFishById(100L)).thenReturn(sampleFish);

            List<InhabitantDetailsDTO> result = inhabitantService.getInhabitantsByAquariumId(10L);

            assertEquals("Nemo", result.get(0).getCommonName());
        }

        @Test
        void returnsEnrichedDtoForCoral() {
            when(inhabitantRepository.findByAquariumId(10L)).thenReturn(List.of(sampleCoralInhabitant));
            when(speciesClient.getCoralById(200L)).thenReturn(sampleCoral);

            List<InhabitantDetailsDTO> result = inhabitantService.getInhabitantsByAquariumId(10L);

            assertEquals(1, result.size());
            assertEquals("Hammer Coral", result.get(0).getCommonName());
            assertEquals("Euphyllia ancora", result.get(0).getScientificName());
            assertEquals("coral", result.get(0).getType());
            assertEquals(sampleCoral, result.get(0).getDetails());
        }

        @Test
        void returnsDtoWithoutSpeciesDetailsWhenSpeciesNotFound() {
            when(inhabitantRepository.findByAquariumId(10L)).thenReturn(List.of(sampleFishInhabitant));
            when(speciesClient.getFishById(100L)).thenThrow(new ResourceNotFoundException("Fish not found"));

            List<InhabitantDetailsDTO> result = inhabitantService.getInhabitantsByAquariumId(10L);

            assertEquals(1, result.size());
            assertNull(result.get(0).getCommonName());
            assertNull(result.get(0).getDetails());
        }
    }

    @Nested
    class AddInhabitant {

        @Test
        void savesAndReturnsDtoForFish() {
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            dto.setInhabitantType(InhabitantType.FISH);
            dto.setInhabitantId(100L);
            dto.setQuantity(1);

            when(speciesClient.getFishById(100L)).thenReturn(sampleFish);
            when(inhabitantRepository.save(any(Inhabitant.class))).thenAnswer(i -> i.getArgument(0));

            InhabitantDetailsDTO result = inhabitantService.addInhabitant(10L, dto);

            assertEquals("fish", result.getType());
            verify(inhabitantRepository).save(any(Inhabitant.class));
        }

        @Test
        void throwsWhenSpeciesNotFound() {
            CreateInhabitantDTO dto = new CreateInhabitantDTO();
            dto.setInhabitantType(InhabitantType.FISH);
            dto.setInhabitantId(999L);

            when(speciesClient.getFishById(999L)).thenThrow(new ResourceNotFoundException("Fish not found"));

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

            assertThrows(IllegalArgumentException.class,
                    () -> inhabitantService.removeInhabitant(99L, 1L));
            verify(inhabitantRepository, never()).deleteById(any());
        }
    }

    @Nested
    class UpdateInhabitant {

        @Test
        void updatesOnlyNonNullFieldsAndReturnsDto() {
            when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(sampleFishInhabitant));
            when(inhabitantRepository.save(any(Inhabitant.class))).thenAnswer(i -> i.getArgument(0));

            UpdateInhabitantDTO dto = new UpdateInhabitantDTO();
            dto.setQuantity(5);
            dto.setNotes("Updated notes");

            InhabitantDetailsDTO result = inhabitantService.updateInhabitant(10L, 1L, dto);

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
