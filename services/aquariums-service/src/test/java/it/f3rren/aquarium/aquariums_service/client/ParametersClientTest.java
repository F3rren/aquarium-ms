package it.f3rren.aquarium.aquariums_service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.TargetParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.WaterParameterDTO;

/**
 * Unit tests for ParametersClient.
 * Tests happy-path HTTP calls by mocking the RestClient fluent chain.
 * Circuit breaker / retry behavior requires a Spring context — see integration tests.
 */
@ExtendWith(MockitoExtension.class)
class ParametersClientTest {

    @Mock private RestClient waterRestClient;
    @Mock private RestClient manualRestClient;
    @Mock private RestClient targetRestClient;

    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersUriSpec uriSpec;
    private RestClient.RequestHeadersSpec<?> headersSpec;
    private RestClient.ResponseSpec responseSpec;

    private ParametersClient parametersClient;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @BeforeEach
    void setUp() {
        uriSpec      = mock(RestClient.RequestHeadersUriSpec.class);
        headersSpec  = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        parametersClient = new ParametersClient(waterRestClient, manualRestClient, targetRestClient);
    }

    // ========================
    // Water Parameters
    // ========================

    @Nested
    @DisplayName("getLatestWaterParameter")
    class GetLatestWaterParameter {

        @Test
        @DisplayName("should return response from water-parameters service")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnLatestWaterParameter() {
            WaterParameterDTO expected = new WaterParameterDTO();
            expected.setAquariumId(1L);
            expected.setTemperature(25.0);

            ApiResponseDTO<WaterParameterDTO> apiResponse =
                    new ApiResponseDTO<>(true, "ok", expected, null);

            when(waterRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(anyString(), any(Object.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<WaterParameterDTO> result = parametersClient.getLatestWaterParameter(1L);

            assertThat(result.getSuccess()).isTrue();
            assertThat(result.getData().getTemperature()).isEqualTo(25.0);
        }
    }

    // ========================
    // Manual Parameters
    // ========================

    @Nested
    @DisplayName("getLatestManualParameter")
    class GetLatestManualParameter {

        @Test
        @DisplayName("should return response from manual-parameters service")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnLatestManualParameter() {
            ManualParameterDTO expected = new ManualParameterDTO();
            expected.setAquariumId(1L);
            expected.setCalcium(420.0);

            ApiResponseDTO<ManualParameterDTO> apiResponse =
                    new ApiResponseDTO<>(true, "ok", expected, null);

            when(manualRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(anyString(), any(Object.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<ManualParameterDTO> result = parametersClient.getLatestManualParameter(1L);

            assertThat(result.getSuccess()).isTrue();
            assertThat(result.getData().getCalcium()).isEqualTo(420.0);
        }
    }

    // ========================
    // Target Parameters
    // ========================

    @Nested
    @DisplayName("getTargetParameters")
    class GetTargetParameters {

        @Test
        @DisplayName("should return response from target-parameters service")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnTargetParameters() {
            TargetParameterDTO expected = TargetParameterDTO.builder()
                    .aquariumId(1L)
                    .temperature(26.0)
                    .ph(8.2)
                    .build();

            ApiResponseDTO<TargetParameterDTO> apiResponse =
                    new ApiResponseDTO<>(true, "ok", expected, null);

            when(targetRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(anyString(), any(Object.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<TargetParameterDTO> result = parametersClient.getTargetParameters(1L);

            assertThat(result.getSuccess()).isTrue();
            assertThat(result.getData().getPh()).isEqualTo(8.2);
        }
    }

    // ========================
    // Fallback response structure
    // ========================

    @Nested
    @DisplayName("Fallback response structure")
    class FallbackStructure {

        @Test
        @DisplayName("single-item fallback should have success=false and null data")
        void singleItemFallbackShouldHaveNullData() {
            ApiResponseDTO<WaterParameterDTO> fallback =
                    new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);

            assertThat(fallback.getSuccess()).isFalse();
            assertThat(fallback.getData()).isNull();
            assertThat(fallback.getMessage()).contains("unavailable");
        }

        @Test
        @DisplayName("list fallback should have success=false and null data (not empty list)")
        void listFallbackShouldHaveNullData() {
            ApiResponseDTO<List<WaterParameterDTO>> fallback =
                    new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);

            assertThat(fallback.getSuccess()).isFalse();
            assertThat(fallback.getData()).isNull();
        }
    }
}
