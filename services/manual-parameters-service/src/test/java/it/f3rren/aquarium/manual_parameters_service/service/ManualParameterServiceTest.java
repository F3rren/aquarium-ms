package it.f3rren.aquarium.manual_parameters_service.service;

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

import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.manual_parameters_service.model.ManualParameter;
import it.f3rren.aquarium.manual_parameters_service.repository.IManualParameterRepository;

@ExtendWith(MockitoExtension.class)
class ManualParameterServiceTest {

    @Mock
    private IManualParameterRepository manualParameterRepository;

    @InjectMocks
    private ManualParameterService manualParameterService;

    @Test
    void saveManualParameter_shouldSaveAndReturn() {
        CreateManualParameterDTO dto = new CreateManualParameterDTO();
        dto.setCalcium(420.0);
        dto.setMagnesium(1300.0);
        dto.setKh(8.0);
        dto.setNitrate(5.0);
        dto.setPhosphate(0.05);
        dto.setNotes("test note");

        ManualParameter saved = new ManualParameter();
        saved.setId(1L);
        saved.setAquariumId(1L);
        saved.setCalcium(420.0);
        saved.setMagnesium(1300.0);

        when(manualParameterRepository.save(any(ManualParameter.class))).thenReturn(saved);

        ManualParameter result = manualParameterService.saveManualParameter(1L, dto);

        assertNotNull(result);
        assertEquals(420.0, result.getCalcium());
        verify(manualParameterRepository, times(1)).save(any(ManualParameter.class));
    }

    @Test
    void getLatestManualParameter_shouldReturnLatest() {
        ManualParameter latest = new ManualParameter();
        latest.setId(1L);
        latest.setAquariumId(1L);
        latest.setMeasuredAt(LocalDateTime.now());

        when(manualParameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(1L)).thenReturn(latest);

        ManualParameter result = manualParameterService.getLatestManualParameter(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAquariumId());
    }

    @Test
    void getLatestManualParameter_shouldThrowWhenNotFound() {
        when(manualParameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(99L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> manualParameterService.getLatestManualParameter(99L));
    }

    @Test
    void getAllManualParameters_shouldReturnList() {
        List<ManualParameter> params = List.of(new ManualParameter(), new ManualParameter());
        when(manualParameterRepository.findByAquariumIdOrderByMeasuredAtDesc(1L)).thenReturn(params);

        List<ManualParameter> result = manualParameterService.getAllManualParameters(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getManualParametersHistory_shouldReturnFilteredList() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();
        List<ManualParameter> params = List.of(new ManualParameter());

        when(manualParameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                eq(1L), eq(from), eq(to))).thenReturn(params);

        List<ManualParameter> result = manualParameterService.getManualParametersHistory(1L, from, to);

        assertFalse(result.isEmpty());
    }

    @Test
    void getAllManualParameters_shouldReturnEmptyList() {
        when(manualParameterRepository.findByAquariumIdOrderByMeasuredAtDesc(99L))
                .thenReturn(Collections.emptyList());

        List<ManualParameter> result = manualParameterService.getAllManualParameters(99L);

        assertTrue(result.isEmpty());
    }
}
