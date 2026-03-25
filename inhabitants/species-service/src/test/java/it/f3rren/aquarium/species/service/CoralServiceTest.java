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
import it.f3rren.aquarium.species.model.Coral;
import it.f3rren.aquarium.species.repository.ICoralRepository;

@ExtendWith(MockitoExtension.class)
class CoralServiceTest {

    @Mock
    private ICoralRepository coralRepository;

    @InjectMocks
    private CoralService coralService;

    @Test
    void getAllCorals_returnsList() {
        Coral c1 = new Coral(1L, "Hammer Coral", "Euphyllia ancora", "LPS", 100, 30, "Medium", "Medium", "Medium", "Middle", false, "Meaty foods", "A popular LPS coral");
        Coral c2 = new Coral(2L, "Torch Coral", "Euphyllia glabrescens", "LPS", 100, 25, "Medium", "Low", "Low", "Bottom", false, "Meaty foods", "A flowing LPS coral");
        when(coralRepository.findAllSortedByName()).thenReturn(List.of(c1, c2));

        List<Coral> result = coralService.getAllCorals();

        assertEquals(2, result.size());
        verify(coralRepository).findAllSortedByName();
    }

    @Test
    void getCoralById_returnsCoral() {
        Coral coral = new Coral(1L, "Hammer Coral", "Euphyllia ancora", "LPS", 100, 30, "Medium", "Medium", "Medium", "Middle", false, "Meaty foods", "A popular LPS coral");
        when(coralRepository.findById(1L)).thenReturn(Optional.of(coral));

        Coral result = coralService.getCoralById(1L);

        assertEquals("Hammer Coral", result.getCommonName());
    }

    @Test
    void getCoralById_throwsWhenNotFound() {
        when(coralRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coralService.getCoralById(99L));
    }
}
