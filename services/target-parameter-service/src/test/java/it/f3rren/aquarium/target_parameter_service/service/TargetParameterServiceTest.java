package it.f3rren.aquarium.target_parameter_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.dto.TargetParameterResponseDTO;
import it.f3rren.aquarium.target_parameter_service.model.TargetParameter;
import it.f3rren.aquarium.target_parameter_service.repository.ITargetParameterRepository;

@ExtendWith(MockitoExtension.class)
class TargetParameterServiceTest {

    @Mock
    private ITargetParameterRepository targetParameterRepository;

    @InjectMocks
    private TargetParameterService targetParameterService;

    @Nested
    class GetTargetParameters {

        @Test
        void returnsDto() {
            TargetParameter target = new TargetParameter(1L, 1L, 25.0, 8.2, 35.0, 350.0);
            when(targetParameterRepository.findByAquariumId(1L)).thenReturn(Optional.of(target));

            TargetParameterResponseDTO result = targetParameterService.getTargetParameters(1L);

            assertNotNull(result);
            assertEquals(25.0, result.getTemperature());
            assertEquals(8.2, result.getPh());
        }

        @Test
        void returnsNullWhenNotConfigured() {
            when(targetParameterRepository.findByAquariumId(99L)).thenReturn(Optional.empty());

            TargetParameterResponseDTO result = targetParameterService.getTargetParameters(99L);

            assertNull(result);
        }
    }

    @Nested
    class SaveTargetParameters {

        @Test
        void createsWhenNotExists() {
            SaveTargetParameterDTO dto = new SaveTargetParameterDTO();
            dto.setTemperature(25.0);
            dto.setPh(8.2);
            dto.setSalinity(35.0);
            dto.setOrp(350.0);

            TargetParameter saved = new TargetParameter(1L, 1L, 25.0, 8.2, 35.0, 350.0);

            when(targetParameterRepository.findByAquariumId(1L)).thenReturn(Optional.empty());
            when(targetParameterRepository.save(any(TargetParameter.class))).thenReturn(saved);

            TargetParameterResponseDTO result = targetParameterService.saveTargetParameters(1L, dto);

            assertNotNull(result);
            assertEquals(25.0, result.getTemperature());
            verify(targetParameterRepository).save(any(TargetParameter.class));
        }

        @Test
        void updatesWhenExists() {
            SaveTargetParameterDTO dto = new SaveTargetParameterDTO();
            dto.setTemperature(26.0);
            dto.setPh(8.3);
            dto.setSalinity(36.0);
            dto.setOrp(360.0);

            TargetParameter existing = new TargetParameter(1L, 1L, 25.0, 8.2, 35.0, 350.0);
            TargetParameter updated = new TargetParameter(1L, 1L, 26.0, 8.3, 36.0, 360.0);

            when(targetParameterRepository.findByAquariumId(1L)).thenReturn(Optional.of(existing));
            when(targetParameterRepository.save(existing)).thenReturn(updated);

            TargetParameterResponseDTO result = targetParameterService.saveTargetParameters(1L, dto);

            assertNotNull(result);
            assertEquals(26.0, result.getTemperature());
            verify(targetParameterRepository).save(existing);
        }
    }
}
