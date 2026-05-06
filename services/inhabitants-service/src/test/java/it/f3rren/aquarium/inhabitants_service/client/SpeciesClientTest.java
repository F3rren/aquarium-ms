package it.f3rren.aquarium.inhabitants_service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import it.f3rren.aquarium.inhabitants_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.inhabitants_service.dto.CoralDTO;
import it.f3rren.aquarium.inhabitants_service.dto.FishDTO;
import it.f3rren.aquarium.inhabitants_service.exception.ResourceNotFoundException;

/**
 * Unit tests for SpeciesClient.
 * RestTemplate is mocked; speciesServiceUrl is injected via ReflectionTestUtils.
 */
@ExtendWith(MockitoExtension.class)
class SpeciesClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SpeciesClient speciesClient;

    private static final String BASE_URL = "http://localhost:9999/species";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(speciesClient, "speciesServiceUrl", BASE_URL);
    }

    // ========================
    // getFishById
    // ========================

    @Nested
    @DisplayName("getFishById")
    class GetFishById {

        @Test
        @DisplayName("should return fish when service responds with success=true and non-null data")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnFishOnSuccess() {
            FishDTO fish = new FishDTO();
            fish.setId(1L);
            fish.setCommonName("Clownfish");
            fish.setScientificName("Amphiprioninae");

            ApiResponseDTO<FishDTO> apiResponse = new ApiResponseDTO<>(true, "ok", fish, null);

            when(restTemplate.exchange(
                    eq(BASE_URL + "/fish/1"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(ResponseEntity.ok(apiResponse));

            FishDTO result = speciesClient.getFishById(1L);

            assertThat(result.getCommonName()).isEqualTo("Clownfish");
            assertThat(result.getScientificName()).isEqualTo("Amphiprioninae");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when response body is null")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldThrowWhenBodyIsNull() {
            when(restTemplate.exchange(
                    eq(BASE_URL + "/fish/1"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(ResponseEntity.ok(null));

            assertThrows(ResourceNotFoundException.class, () -> speciesClient.getFishById(1L));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when success=false")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldThrowWhenSuccessIsFalse() {
            ApiResponseDTO<FishDTO> apiResponse = new ApiResponseDTO<>(false, "error", null, null);

            when(restTemplate.exchange(
                    eq(BASE_URL + "/fish/1"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(ResponseEntity.ok(apiResponse));

            assertThrows(ResourceNotFoundException.class, () -> speciesClient.getFishById(1L));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when data is null")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldThrowWhenDataIsNull() {
            ApiResponseDTO<FishDTO> apiResponse = new ApiResponseDTO<>(true, "ok", null, null);

            when(restTemplate.exchange(
                    eq(BASE_URL + "/fish/1"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(ResponseEntity.ok(apiResponse));

            assertThrows(ResourceNotFoundException.class, () -> speciesClient.getFishById(1L));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException on HTTP 404")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldThrowResourceNotFoundOnHttp404() {
            when(restTemplate.exchange(
                    eq(BASE_URL + "/fish/1"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenThrow(HttpClientErrorException.create(
                    HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

            ResourceNotFoundException ex =
                    assertThrows(ResourceNotFoundException.class, () -> speciesClient.getFishById(1L));

            assertTrue(ex.getMessage().contains("1"));
        }

        @Test
        @DisplayName("should throw RuntimeException with 'unavailable' message when service is unreachable")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldThrowRuntimeExceptionWhenServiceUnreachable() {
            when(restTemplate.exchange(
                    eq(BASE_URL + "/fish/1"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenThrow(new ResourceAccessException("Connection refused"));

            RuntimeException ex =
                    assertThrows(RuntimeException.class, () -> speciesClient.getFishById(1L));

            assertTrue(ex.getMessage().contains("unavailable"));
        }
    }

    // ========================
    // getCoralById
    // ========================

    @Nested
    @DisplayName("getCoralById")
    class GetCoralById {

        @Test
        @DisplayName("should return coral when service responds with success=true and non-null data")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldReturnCoralOnSuccess() {
            CoralDTO coral = new CoralDTO();
            coral.setId(2L);
            coral.setCommonName("Hammer Coral");
            coral.setScientificName("Euphyllia ancora");

            ApiResponseDTO<CoralDTO> apiResponse = new ApiResponseDTO<>(true, "ok", coral, null);

            when(restTemplate.exchange(
                    eq(BASE_URL + "/corals/2"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenReturn(ResponseEntity.ok(apiResponse));

            CoralDTO result = speciesClient.getCoralById(2L);

            assertThat(result.getCommonName()).isEqualTo("Hammer Coral");
            assertThat(result.getScientificName()).isEqualTo("Euphyllia ancora");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException on HTTP 404 for coral")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldThrowResourceNotFoundOnHttp404() {
            when(restTemplate.exchange(
                    eq(BASE_URL + "/corals/2"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenThrow(HttpClientErrorException.create(
                    HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

            assertThrows(ResourceNotFoundException.class, () -> speciesClient.getCoralById(2L));
        }

        @Test
        @DisplayName("should throw RuntimeException when coral service is unreachable")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void shouldThrowRuntimeExceptionWhenServiceUnreachable() {
            when(restTemplate.exchange(
                    eq(BASE_URL + "/corals/2"),
                    eq(HttpMethod.GET),
                    isNull(),
                    any(ParameterizedTypeReference.class)
            )).thenThrow(new ResourceAccessException("Timeout"));

            RuntimeException ex =
                    assertThrows(RuntimeException.class, () -> speciesClient.getCoralById(2L));

            assertTrue(ex.getMessage().contains("unavailable"));
        }
    }
}
