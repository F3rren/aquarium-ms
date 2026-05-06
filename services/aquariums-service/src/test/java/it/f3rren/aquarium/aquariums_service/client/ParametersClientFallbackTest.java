package it.f3rren.aquarium.aquariums_service.client;

import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.TargetParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.WaterParameterDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that all circuit-breaker fallback methods in ParametersClient are reachable.
 * All three circuit breakers are forced open so Resilience4j invokes each private
 * fallback directly, without making any real HTTP call.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.kafka.admin.auto-create=false",
    "resilience4j.circuitbreaker.instances.waterParameters.force-open=true",
    "resilience4j.circuitbreaker.instances.manualParameters.force-open=true",
    "resilience4j.circuitbreaker.instances.targetParameters.force-open=true"
})
class ParametersClientFallbackTest {

    @Autowired
    private ParametersClient parametersClient;

    // ========================
    // Water Parameters fallbacks
    // ========================

    @Nested
    @DisplayName("Water parameters fallbacks (circuit breaker force-open)")
    class WaterParameterFallbacks {

        @Test
        @DisplayName("fallbackAddWaterParameter: returns success=false with unavailable message")
        void fallbackAddWaterParameter() {
            ApiResponseDTO<WaterParameterDTO> result =
                    parametersClient.addWaterParameter(1L, new WaterParameterDTO());

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("fallbackGetWaterParametersByAquarium: returns success=false with unavailable message")
        void fallbackGetWaterParametersByAquarium() {
            ApiResponseDTO<List<WaterParameterDTO>> result =
                    parametersClient.getWaterParametersByAquarium(1L, 10);

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("fallbackGetLatestWaterParameter: returns success=false with unavailable message")
        void fallbackGetLatestWaterParameter() {
            ApiResponseDTO<WaterParameterDTO> result =
                    parametersClient.getLatestWaterParameter(1L);

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("fallbackGetWaterParametersHistory: returns success=false with unavailable message")
        void fallbackGetWaterParametersHistory() {
            ApiResponseDTO<List<WaterParameterDTO>> result =
                    parametersClient.getWaterParametersHistory(1L, "week", null, null);

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }
    }

    // ========================
    // Manual Parameters fallbacks
    // ========================

    @Nested
    @DisplayName("Manual parameters fallbacks (circuit breaker force-open)")
    class ManualParameterFallbacks {

        @Test
        @DisplayName("fallbackAddManualParameter: returns success=false with unavailable message")
        void fallbackAddManualParameter() {
            ApiResponseDTO<ManualParameterDTO> result =
                    parametersClient.addManualParameter(1L, new ManualParameterDTO());

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("fallbackGetLatestManualParameter: returns success=false with unavailable message")
        void fallbackGetLatestManualParameter() {
            ApiResponseDTO<ManualParameterDTO> result =
                    parametersClient.getLatestManualParameter(1L);

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("fallbackGetAllManualParameters: returns success=false with unavailable message")
        void fallbackGetAllManualParameters() {
            ApiResponseDTO<List<ManualParameterDTO>> result =
                    parametersClient.getAllManualParameters(1L);

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("fallbackGetManualParametersHistory: returns success=false with unavailable message")
        void fallbackGetManualParametersHistory() {
            ApiResponseDTO<List<ManualParameterDTO>> result =
                    parametersClient.getManualParametersHistory(1L, "2025-01-01", "2025-12-31");

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }
    }

    // ========================
    // Target Parameters fallbacks
    // ========================

    @Nested
    @DisplayName("Target parameters fallbacks (circuit breaker force-open)")
    class TargetParameterFallbacks {

        @Test
        @DisplayName("fallbackGetTargetParameters: returns success=false with unavailable message")
        void fallbackGetTargetParameters() {
            ApiResponseDTO<TargetParameterDTO> result =
                    parametersClient.getTargetParameters(1L);

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("fallbackSaveTargetParameters: returns success=false with unavailable message")
        void fallbackSaveTargetParameters() {
            ApiResponseDTO<TargetParameterDTO> result =
                    parametersClient.saveTargetParameters(
                            1L,
                            TargetParameterDTO.builder().temperature(26.0).build());

            assertThat(result.getSuccess()).isFalse();
            assertThat(result.getMessage()).contains("unavailable");
        }
    }
}
