package it.f3rren.aquarium.species.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.species.exception.ResourceNotFoundException;
import it.f3rren.aquarium.species.model.Fish;
import it.f3rren.aquarium.species.repository.IFishRepository;

@ExtendWith(MockitoExtension.class)
class FishServiceTest {

    @Mock
    private IFishRepository fishRepository;

    @InjectMocks
    private FishService fishService;

    @Test
    void getAllFish_returnsList() {
        Fish f1 = new Fish(1L, "Clownfish", "Amphiprioninae", "Pomacentridae", 50, 11, "Easy", true, "Peaceful", "Omnivore", null, null, "Marine");
        Fish f2 = new Fish(2L, "Tang", "Acanthuridae", "Acanthuridae", 200, 40, "Medium", true, "Semi-aggressive", "Herbivore", null, null, "Marine");
        when(fishRepository.findAllSortedByName()).thenReturn(List.of(f1, f2));

        List<Fish> result = fishService.getAllFish();

        assertEquals(2, result.size());
        verify(fishRepository).findAllSortedByName();
    }

    @Test
    void getFishById_returnsfish() {
        Fish fish = new Fish(1L, "Clownfish", "Amphiprioninae", "Pomacentridae", 50, 11, "Easy", true, "Peaceful", "Omnivore", null, null, "Marine");
        when(fishRepository.findById(1L)).thenReturn(Optional.of(fish));

        Fish result = fishService.getFishById(1L);

        assertEquals("Clownfish", result.getCommonName());
    }

    @Test
    void getFishById_throwsWhenNotFound() {
        when(fishRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fishService.getFishById(99L));
    }
}
