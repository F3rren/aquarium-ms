package it.f3rren.aquarium.manual_parameters_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.manual_parameters_service.model.ManualParameter;
import it.f3rren.aquarium.manual_parameters_service.repository.IManualParameterRepository;

@ExtendWith(MockitoExtension.class)
class ManualParameterServiceTest {

    @Mock
    private IManualParameterRepository manualParameterRepository;

    @InjectMocks
    private ManualParameterService manualParameterService;

    private ManualParameter sampleParameter;

    @BeforeEach
    void setUp() {
        sampleParameter = new ManualParameter();
        sampleParameter.setId(1L);
        sampleParameter.setAquariumId(1L);
        sampleParameter.setCalcium(420.0);
        sampleParameter.setMagnesium(1300.0);
        sampleParameter.setMeasuredAt(LocalDateTime.now());
    }

    @Nested
    class SaveManualParameter {

        @Test
        void savesAndReturnsDto() {
            CreateManualParameterDTO dto = new CreateManualParameterDTO();
            dto.setCalcium(420.0);
            dto.setMagnesium(1300.0);
            dto.setKh(8.0);
            dto.setNitrate(5.0);
            dto.setPhosphate(0.05);
            dto.setNotes("test note");

            when(manualParameterRepository.save(any(ManualParameter.class))).thenReturn(sampleParameter);

            ManualParameterDTO result = manualParameterService.saveManualParameter(1L, dto);

            assertNotNull(result);
            assertEquals(420.0, result.getCalcium());
            verify(manualParameterRepository).save(any(ManualParameter.class));
        }
    }

    @Nested
    class GetLatestManualParameter {

        @Test
        void returnsLatestDto() {
            when(manualParameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(1L))
                    .thenReturn(Optional.of(sampleParameter));

            ManualParameterDTO result = manualParameterService.getLatestManualParameter(1L);

            assertNotNull(result);
            assertEquals(1L, result.getAquariumId());
            assertEquals(420.0, result.getCalcium());
        }

        @Test
        void throwsWhenNotFound() {
            when(manualParameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> manualParameterService.getLatestManualParameter(99L));
        }
    }

    @Nested
    class GetAllManualParameters {

        @Test
        void returnsDtoList() {
            when(manualParameterRepository.findByAquariumIdOrderByMeasuredAtDesc(1L))
                    .thenReturn(List.of(sampleParameter, sampleParameter));

            List<ManualParameterDTO> result = manualParameterService.getAllManualParameters(1L);

            assertEquals(2, result.size());
        }

        @Test
        void returnsEmptyList() {
            when(manualParameterRepository.findByAquariumIdOrderByMeasuredAtDesc(99L))
                    .thenReturn(Collections.emptyList());

            List<ManualParameterDTO> result = manualParameterService.getAllManualParameters(99L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetManualParametersHistory {

        @Test
        void returnsFilteredDtoList() {
            LocalDateTime from = LocalDateTime.now().minusDays(7);
            LocalDateTime to = LocalDateTime.now();

            when(manualParameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq(1L), eq(from), eq(to))).thenReturn(List.of(sampleParameter));

            List<ManualParameterDTO> result = manualParameterService.getManualParametersHistory(1L, from, to);

            assertFalse(result.isEmpty());
            assertEquals(420.0, result.get(0).getCalcium());
        }
    }
}
