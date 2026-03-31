package it.f3rren.aquarium.water_parameters_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.parameters_service.model.Parameter;
import it.f3rren.aquarium.parameters_service.repository.IParameterRepository;
import it.f3rren.aquarium.parameters_service.service.ParameterService;

@ExtendWith(MockitoExtension.class)
class ParameterServiceTest {

    @Mock
    private IParameterRepository parameterRepository;

    @InjectMocks
    private ParameterService parameterService;

    @Test
    void saveParameter_shouldSaveAndReturn() {
        CreateParameterDTO dto = new CreateParameterDTO();
        dto.setTemperature(25.0);
        dto.setPh(8.2);
        dto.setSalinity(35);
        dto.setOrp(350);

        Parameter saved = new Parameter();
        saved.setId(1L);
        saved.setAquariumId(1L);
        saved.setTemperature(25.0);
        saved.setPh(8.2);
        saved.setSalinity(35);
        saved.setOrp(350);

        when(parameterRepository.save(any(Parameter.class))).thenReturn(saved);

        Parameter result = parameterService.saveParameter(1L, dto);

        assertNotNull(result);
        assertEquals(25.0, result.getTemperature());
        assertEquals(8.2, result.getPh());
        verify(parameterRepository, times(1)).save(any(Parameter.class));
    }

    @Test
    void getLatestParameter_shouldReturnLatest() {
        Parameter latest = new Parameter();
        latest.setId(1L);
        latest.setAquariumId(1L);
        latest.setMeasuredAt(LocalDateTime.now());

        when(parameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(1L)).thenReturn(latest);

        Parameter result = parameterService.getLatestParameter(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAquariumId());
    }

    @Test
    void getLatestParameter_shouldThrowWhenNotFound() {
        when(parameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(99L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> parameterService.getLatestParameter(99L));
    }

    @Test
    void getParametersByAquariumId_shouldThrowWhenEmpty() {
        when(parameterRepository.findByAquariumIdOrderByMeasuredAtDesc(99L))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> parameterService.getParametersByAquariumId(99L, 10));
    }

    @Test
    void getParametersByPeriod_week_shouldReturnList() {
        List<Parameter> params = List.of(new Parameter());
        when(parameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(params);

        List<Parameter> result = parameterService.getParametersByPeriod(1L, "week");

        assertFalse(result.isEmpty());
    }
}
