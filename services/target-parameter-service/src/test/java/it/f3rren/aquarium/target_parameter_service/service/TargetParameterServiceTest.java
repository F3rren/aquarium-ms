package it.f3rren.aquarium.target_parameter_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.model.TargetParameter;
import it.f3rren.aquarium.target_parameter_service.repository.ITargetParameterRepository;

@ExtendWith(MockitoExtension.class)
class TargetParameterServiceTest {

    @Mock
    private ITargetParameterRepository targetParameterRepository;

    @InjectMocks
    private TargetParameterService targetParameterService;

    @Test
    void getTargetParameters_shouldReturnWhenExists() {
        TargetParameter target = new TargetParameter();
        target.setId(1L);
        target.setAquariumId(1L);
        target.setTemperature(25.0);

        when(targetParameterRepository.findByAquariumId(1L)).thenReturn(target);

        TargetParameter result = targetParameterService.getTargetParameters(1L);

        assertNotNull(result);
        assertEquals(25.0, result.getTemperature());
    }

    @Test
    void getTargetParameters_shouldReturnNullWhenNotFound() {
        when(targetParameterRepository.findByAquariumId(99L)).thenReturn(null);

        TargetParameter result = targetParameterService.getTargetParameters(99L);

        assertNull(result);
    }

    @Test
    void saveTargetParameters_shouldCreateWhenNotExists() {
        SaveTargetParameterDTO dto = new SaveTargetParameterDTO();
        dto.setTemperature(25.0);
        dto.setPh(8.2);
        dto.setSalinity(35.0);
        dto.setOrp(350.0);

        TargetParameter saved = new TargetParameter();
        saved.setId(1L);
        saved.setAquariumId(1L);
        saved.setTemperature(25.0);

        when(targetParameterRepository.findByAquariumId(1L)).thenReturn(null);
        when(targetParameterRepository.save(any(TargetParameter.class))).thenReturn(saved);

        TargetParameter result = targetParameterService.saveTargetParameters(1L, dto);

        assertNotNull(result);
        verify(targetParameterRepository, times(1)).save(any(TargetParameter.class));
    }

    @Test
    void saveTargetParameters_shouldUpdateWhenExists() {
        SaveTargetParameterDTO dto = new SaveTargetParameterDTO();
        dto.setTemperature(26.0);
        dto.setPh(8.3);
        dto.setSalinity(36.0);
        dto.setOrp(360.0);

        TargetParameter existing = new TargetParameter();
        existing.setId(1L);
        existing.setAquariumId(1L);
        existing.setTemperature(25.0);

        when(targetParameterRepository.findByAquariumId(1L)).thenReturn(existing);
        when(targetParameterRepository.save(any(TargetParameter.class))).thenReturn(existing);

        TargetParameter result = targetParameterService.saveTargetParameters(1L, dto);

        assertNotNull(result);
        verify(targetParameterRepository, times(1)).save(existing);
    }
}
