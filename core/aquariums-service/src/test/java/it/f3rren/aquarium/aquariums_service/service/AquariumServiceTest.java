package it.f3rren.aquarium.aquariums_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.aquariums_service.dto.CreateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.dto.UpdateAquariumDTO;
import it.f3rren.aquarium.aquariums_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.aquariums_service.model.Aquarium;
import it.f3rren.aquarium.aquariums_service.repository.IAquariumRepository;

/**
 * Unit tests for AquariumService.
 * Uses Mockito to mock the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class AquariumServiceTest {

    @Mock
    private IAquariumRepository aquariumRepository;

    @InjectMocks
    private AquariumService aquariumService;

    private Aquarium sampleAquarium;

    @BeforeEach
    void setUp() {
        sampleAquarium = new Aquarium();
        sampleAquarium.setId(1L);
        sampleAquarium.setName("Reef Tank");
        sampleAquarium.setVolume(200);
        sampleAquarium.setType("saltwater");
        sampleAquarium.setDescription("A beautiful reef aquarium");
    }

    // ========================
    // createAquarium
    // ========================

    @Nested
    @DisplayName("createAquarium")
    class CreateAquarium {

        @Test
        @DisplayName("should create an aquarium successfully")
        void shouldCreateAquarium() {
            CreateAquariumDTO dto = new CreateAquariumDTO();
            dto.setName("  Reef Tank  ");
            dto.setVolume(200);
            dto.setType("saltwater");
            dto.setDescription("A beautiful reef aquarium");

            when(aquariumRepository.save(any(Aquarium.class))).thenReturn(sampleAquarium);

            Aquarium result = aquariumService.createAquarium(dto);

            assertNotNull(result);
            assertEquals("Reef Tank", result.getName());
            assertEquals(200, result.getVolume());
            verify(aquariumRepository, times(1)).save(any(Aquarium.class));
        }

        @Test
        @DisplayName("should trim name before saving")
        void shouldTrimName() {
            CreateAquariumDTO dto = new CreateAquariumDTO();
            dto.setName("  My Tank  ");
            dto.setVolume(100);
            dto.setType("freshwater");

            when(aquariumRepository.save(any(Aquarium.class))).thenAnswer(invocation -> {
                Aquarium saved = invocation.getArgument(0);
                assertEquals("My Tank", saved.getName());
                saved.setId(2L);
                return saved;
            });

            aquariumService.createAquarium(dto);
            verify(aquariumRepository).save(any(Aquarium.class));
        }
    }

    // ========================
    // getAllAquariums
    // ========================

    @Nested
    @DisplayName("getAllAquariums")
    class GetAllAquariums {

        @Test
        @DisplayName("should return all aquariums")
        void shouldReturnAllAquariums() {
            Aquarium second = new Aquarium();
            second.setId(2L);
            second.setName("Freshwater Tank");
            second.setVolume(100);
            second.setType("freshwater");

            when(aquariumRepository.findAll()).thenReturn(List.of(sampleAquarium, second));

            List<Aquarium> result = aquariumService.getAllAquariums();

            assertEquals(2, result.size());
            verify(aquariumRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("should return empty list when no aquariums exist")
        void shouldReturnEmptyList() {
            when(aquariumRepository.findAll()).thenReturn(List.of());

            List<Aquarium> result = aquariumService.getAllAquariums();

            assertTrue(result.isEmpty());
        }
    }

    // ========================
    // getAquariumById
    // ========================

    @Nested
    @DisplayName("getAquariumById")
    class GetAquariumById {

        @Test
        @DisplayName("should return aquarium when found")
        void shouldReturnAquariumWhenFound() {
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(sampleAquarium));

            Aquarium result = aquariumService.getAquariumById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Reef Tank", result.getName());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(aquariumRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> aquariumService.getAquariumById(99L)
            );

            assertTrue(exception.getMessage().contains("99"));
        }
    }

    // ========================
    // updateAquarium
    // ========================

    @Nested
    @DisplayName("updateAquarium")
    class UpdateAquarium {

        @Test
        @DisplayName("should update only non-null fields (partial update)")
        void shouldPartialUpdate() {
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(sampleAquarium));
            when(aquariumRepository.save(any(Aquarium.class))).thenAnswer(i -> i.getArgument(0));

            UpdateAquariumDTO dto = new UpdateAquariumDTO();
            dto.setName("Updated Name");
            // volume, type, description left null — should not change

            Aquarium result = aquariumService.updateAquarium(1L, dto);

            assertEquals("Updated Name", result.getName());
            assertEquals(200, result.getVolume()); // unchanged
            assertEquals("saltwater", result.getType()); // unchanged
            assertEquals("A beautiful reef aquarium", result.getDescription()); // unchanged
        }

        @Test
        @DisplayName("should update all fields when all provided")
        void shouldUpdateAllFields() {
            when(aquariumRepository.findById(1L)).thenReturn(Optional.of(sampleAquarium));
            when(aquariumRepository.save(any(Aquarium.class))).thenAnswer(i -> i.getArgument(0));

            UpdateAquariumDTO dto = new UpdateAquariumDTO();
            dto.setName("New Name");
            dto.setVolume(500);
            dto.setType("freshwater");
            dto.setDescription("New desc");
            dto.setImageUrl("https://example.com/img.jpg");

            Aquarium result = aquariumService.updateAquarium(1L, dto);

            assertEquals("New Name", result.getName());
            assertEquals(500, result.getVolume());
            assertEquals("freshwater", result.getType());
            assertEquals("New desc", result.getDescription());
            assertEquals("https://example.com/img.jpg", result.getImageUrl());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when aquarium not found")
        void shouldThrowWhenNotFound() {
            when(aquariumRepository.findById(99L)).thenReturn(Optional.empty());

            UpdateAquariumDTO dto = new UpdateAquariumDTO();
            dto.setName("New Name");

            assertThrows(ResourceNotFoundException.class,
                    () -> aquariumService.updateAquarium(99L, dto));
        }
    }

    // ========================
    // deleteAquarium
    // ========================

    @Nested
    @DisplayName("deleteAquarium")
    class DeleteAquarium {

        @Test
        @DisplayName("should delete aquarium when found")
        void shouldDeleteWhenFound() {
            when(aquariumRepository.existsById(1L)).thenReturn(true);

            aquariumService.deleteAquarium(1L);

            verify(aquariumRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(aquariumRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> aquariumService.deleteAquarium(99L));

            verify(aquariumRepository, never()).deleteById(any());
        }
    }
}
