package it.f3rren.aquarium.parameters_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.dto.ParameterDTO;
import it.f3rren.aquarium.parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.parameters_service.model.Parameter;
import it.f3rren.aquarium.parameters_service.repository.IParameterRepository;

@ExtendWith(MockitoExtension.class)
class ParameterServiceTest {

    @Mock
    private IParameterRepository parameterRepository;

    @InjectMocks
    private ParameterService parameterService;

    private Parameter sampleParameter;

    @BeforeEach
    void setUp() {
        sampleParameter = new Parameter();
        sampleParameter.setId(1L);
        sampleParameter.setAquariumId(1L);
        sampleParameter.setTemperature(25.0);
        sampleParameter.setPh(8.2);
        sampleParameter.setSalinity(35);
        sampleParameter.setOrp(350);
        sampleParameter.setMeasuredAt(LocalDateTime.now());
    }

    @Nested
    class SaveParameter {

        @Test
        void savesAndReturnsDto() {
            CreateParameterDTO dto = new CreateParameterDTO();
            dto.setTemperature(25.0);
            dto.setPh(8.2);
            dto.setSalinity(35);
            dto.setOrp(350);

            when(parameterRepository.save(any(Parameter.class))).thenReturn(sampleParameter);

            ParameterDTO result = parameterService.saveParameter(1L, dto);

            assertNotNull(result);
            assertEquals(25.0, result.getTemperature());
            assertEquals(8.2, result.getPh());
            verify(parameterRepository).save(any(Parameter.class));
        }
    }

    @Nested
    class GetLatestParameter {

        @Test
        void returnsLatestDto() {
            when(parameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(1L))
                    .thenReturn(Optional.of(sampleParameter));

            ParameterDTO result = parameterService.getLatestParameter(1L);

            assertNotNull(result);
            assertEquals(1L, result.getAquariumId());
            assertEquals(25.0, result.getTemperature());
        }

        @Test
        void throwsWhenNotFound() {
            when(parameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(99L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> parameterService.getLatestParameter(99L));
        }
    }

    @Nested
    class GetParametersByAquariumId {

        @Test
        void returnsLimitedDtoList() {
            when(parameterRepository.findByAquariumIdOrderByMeasuredAtDesc(1L))
                    .thenReturn(List.of(sampleParameter, sampleParameter, sampleParameter));

            List<ParameterDTO> result = parameterService.getParametersByAquariumId(1L, 2);

            assertEquals(2, result.size());
        }

        @Test
        void returnsEmptyListWhenNoParameters() {
            when(parameterRepository.findByAquariumIdOrderByMeasuredAtDesc(99L))
                    .thenReturn(List.of());

            List<ParameterDTO> result = parameterService.getParametersByAquariumId(99L, 10);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetParametersByPeriod {

        @Test
        void returnsWeekDtoList() {
            when(parameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(sampleParameter));

            List<ParameterDTO> result = parameterService.getParametersByPeriod(1L, "week");

            assertFalse(result.isEmpty());
            assertEquals(25.0, result.get(0).getTemperature());
        }
    }
}
