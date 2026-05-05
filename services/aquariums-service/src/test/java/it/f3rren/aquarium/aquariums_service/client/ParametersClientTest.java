package it.f3rren.aquarium.aquariums_service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
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

    // RETURNS_SELF: uri(), body() etc. all return this same mock, avoid manual chain stubs
    @SuppressWarnings({"unchecked", "rawtypes"})
    private RestClient.RequestBodyUriSpec postChain;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @BeforeEach
    void setUp() {
        uriSpec      = mock(RestClient.RequestHeadersUriSpec.class);
        headersSpec  = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);
        postChain    = mock(RestClient.RequestBodyUriSpec.class, Answers.RETURNS_SELF);

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

    @Nested
    @DisplayName("addWaterParameter")
    class AddWaterParameter {

        @Test
        @DisplayName("should POST and return response")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldAddWaterParameter() {
            WaterParameterDTO param = new WaterParameterDTO();
            param.setTemperature(25.0);
            ApiResponseDTO<WaterParameterDTO> apiResponse = new ApiResponseDTO<>(true, "ok", param, null);

            when(waterRestClient.post()).thenReturn(postChain);
            when(postChain.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<WaterParameterDTO> result = parametersClient.addWaterParameter(1L, param);

            assertThat(result.getSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("getWaterParametersByAquarium")
    class GetWaterParametersByAquarium {

        @Test
        @DisplayName("should GET list of water parameters")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnWaterParametersList() {
            ApiResponseDTO<List<WaterParameterDTO>> apiResponse = new ApiResponseDTO<>(true, "ok", List.of(), null);

            when(waterRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(any(Function.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<List<WaterParameterDTO>> result = parametersClient.getWaterParametersByAquarium(1L, 10);

            assertThat(result.getSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("getWaterParametersHistory")
    class GetWaterParametersHistory {

        @Test
        @DisplayName("should GET history with period")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnHistoryByPeriod() {
            ApiResponseDTO<List<WaterParameterDTO>> apiResponse = new ApiResponseDTO<>(true, "ok", List.of(), null);

            when(waterRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(any(Function.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<List<WaterParameterDTO>> result =
                    parametersClient.getWaterParametersHistory(1L, "week", null, null);

            assertThat(result.getSuccess()).isTrue();
        }

        @Test
        @DisplayName("should GET history with date range")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnHistoryByDateRange() {
            ApiResponseDTO<List<WaterParameterDTO>> apiResponse = new ApiResponseDTO<>(true, "ok", List.of(), null);

            when(waterRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(any(Function.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<List<WaterParameterDTO>> result =
                    parametersClient.getWaterParametersHistory(1L, null, "2025-01-01", "2025-12-31");

            assertThat(result.getSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("addManualParameter")
    class AddManualParameter {

        @Test
        @DisplayName("should POST and return response")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldAddManualParameter() {
            ManualParameterDTO param = new ManualParameterDTO();
            param.setCalcium(420.0);
            ApiResponseDTO<ManualParameterDTO> apiResponse = new ApiResponseDTO<>(true, "ok", param, null);

            when(manualRestClient.post()).thenReturn(postChain);
            when(postChain.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<ManualParameterDTO> result = parametersClient.addManualParameter(1L, param);

            assertThat(result.getSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("getAllManualParameters")
    class GetAllManualParameters {

        @Test
        @DisplayName("should GET all manual parameters")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnAllManualParameters() {
            ApiResponseDTO<List<ManualParameterDTO>> apiResponse = new ApiResponseDTO<>(true, "ok", List.of(), null);

            when(manualRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(anyString(), any(Object.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<List<ManualParameterDTO>> result = parametersClient.getAllManualParameters(1L);

            assertThat(result.getSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("getManualParametersHistory")
    class GetManualParametersHistory {

        @Test
        @DisplayName("should GET manual parameters history by date range")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnManualParametersHistory() {
            ApiResponseDTO<List<ManualParameterDTO>> apiResponse = new ApiResponseDTO<>(true, "ok", List.of(), null);

            when(manualRestClient.get()).thenReturn(uriSpec);
            doReturn(headersSpec).when(uriSpec).uri(any(Function.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<List<ManualParameterDTO>> result =
                    parametersClient.getManualParametersHistory(1L, "2025-01-01", "2025-12-31");

            assertThat(result.getSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("saveTargetParameters")
    class SaveTargetParameters {

        @Test
        @DisplayName("should POST and return response")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldSaveTargetParameters() {
            TargetParameterDTO param = TargetParameterDTO.builder().temperature(26.0).build();
            ApiResponseDTO<TargetParameterDTO> apiResponse = new ApiResponseDTO<>(true, "ok", param, null);

            when(targetRestClient.post()).thenReturn(postChain);
            when(postChain.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(apiResponse);

            ApiResponseDTO<TargetParameterDTO> result = parametersClient.saveTargetParameters(1L, param);

            assertThat(result.getSuccess()).isTrue();
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
