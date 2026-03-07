package it.f3rren.aquarium.aquariums_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.TargetParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.WaterParameterDTO;

import java.util.List;

/**
 * Client class to interact with the parameters microservices.
 * Uses Spring RestClient (synchronous) with Resilience4j circuit breaker and retry
 * for fault-tolerant HTTP requests to water, manual, and target parameters microservices.
 * @author F3rren
 */
@Component
public class ParametersClient {

    private static final Logger log = LoggerFactory.getLogger(ParametersClient.class);

    private static final String WATER_CB = "waterParameters";
    private static final String MANUAL_CB = "manualParameters";
    private static final String TARGET_CB = "targetParameters";

    private final RestClient waterParametersRestClient;
    private final RestClient manualParametersRestClient;
    private final RestClient targetParametersRestClient;

    /**
     * Constructor for ParametersClient.
     * @param waterParametersRestClient RestClient for water parameters microservice
     * @param manualParametersRestClient RestClient for manual parameters microservice
     * @param targetParametersRestClient RestClient for target parameters microservice
     */
    public ParametersClient(
            @Qualifier("waterParametersRestClient") RestClient waterParametersRestClient,
            @Qualifier("manualParametersRestClient") RestClient manualParametersRestClient,
            @Qualifier("targetParametersRestClient") RestClient targetParametersRestClient) {
        this.waterParametersRestClient = waterParametersRestClient;
        this.manualParametersRestClient = manualParametersRestClient;
        this.targetParametersRestClient = targetParametersRestClient;
    }

    // ========================
    // Water Parameters
    // ========================

    /**
     * Adds a new water parameter measurement.
     * @param parameter WaterParameterDTO object to be added
     * @return ApiResponseDTO containing the added water parameter
     */
    @CircuitBreaker(name = WATER_CB, fallbackMethod = "fallbackAddWaterParameter")
    @Retry(name = WATER_CB)
    public ApiResponseDTO<WaterParameterDTO> addWaterParameter(WaterParameterDTO parameter) {
        return waterParametersRestClient.post()
                .uri("/water-parameters")
                .body(parameter)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves water parameters for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param limit Maximum number of parameters to retrieve
     * @return ApiResponseDTO containing a list of water parameters
     */
    @CircuitBreaker(name = WATER_CB, fallbackMethod = "fallbackGetWaterParametersByAquarium")
    @Retry(name = WATER_CB)
    public ApiResponseDTO<List<WaterParameterDTO>> getWaterParametersByAquarium(Long aquariumId, Integer limit) {
        return waterParametersRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/water-parameters/aquarium/{aquariumId}")
                        .queryParam("limit", limit)
                        .build(aquariumId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves the latest water parameter for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @return ApiResponseDTO containing the latest water parameter
     */
    @CircuitBreaker(name = WATER_CB, fallbackMethod = "fallbackGetLatestWaterParameter")
    @Retry(name = WATER_CB)
    public ApiResponseDTO<WaterParameterDTO> getLatestWaterParameter(Long aquariumId) {
        return waterParametersRestClient.get()
                .uri("/water-parameters/aquarium/{aquariumId}/latest", aquariumId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves water parameters history for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param period Time period for the history (e.g., "day", "week", "month")
     * @param from Start date for the history
     * @param to End date for the history
     * @return ApiResponseDTO containing a list of water parameters
     */
    @CircuitBreaker(name = WATER_CB, fallbackMethod = "fallbackGetWaterParametersHistory")
    @Retry(name = WATER_CB)
    public ApiResponseDTO<List<WaterParameterDTO>> getWaterParametersHistory(Long aquariumId, String period, String from, String to) {
        return waterParametersRestClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/water-parameters/aquarium/{aquariumId}/history");
                    if (period != null) {
                        builder.queryParam("period", period);
                    }
                    if (from != null && to != null) {
                        builder.queryParam("from", from).queryParam("to", to);
                    }
                    return builder.build(aquariumId);
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // ========================
    // Manual Parameters
    // ========================

    /**
     * Adds a new manual parameter measurement.
     * @param parameter ManualParameterDTO object to be added
     * @return ApiResponseDTO containing the added manual parameter
     */
    @CircuitBreaker(name = MANUAL_CB, fallbackMethod = "fallbackAddManualParameter")
    @Retry(name = MANUAL_CB)
    public ApiResponseDTO<ManualParameterDTO> addManualParameter(ManualParameterDTO parameter) {
        return manualParametersRestClient.post()
                .uri("/manual-parameters")
                .body(parameter)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves the latest manual parameter for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @return ApiResponseDTO containing the latest manual parameter
     */
    @CircuitBreaker(name = MANUAL_CB, fallbackMethod = "fallbackGetLatestManualParameter")
    @Retry(name = MANUAL_CB)
    public ApiResponseDTO<ManualParameterDTO> getLatestManualParameter(Long aquariumId) {
        return manualParametersRestClient.get()
                .uri("/manual-parameters/aquarium/{aquariumId}/latest", aquariumId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves all manual parameters for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @return ApiResponseDTO containing a list of manual parameters
     */
    @CircuitBreaker(name = MANUAL_CB, fallbackMethod = "fallbackGetAllManualParameters")
    @Retry(name = MANUAL_CB)
    public ApiResponseDTO<List<ManualParameterDTO>> getAllManualParameters(Long aquariumId) {
        return manualParametersRestClient.get()
                .uri("/manual-parameters/aquarium/{aquariumId}", aquariumId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves manual parameters history for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param from Start date for the history
     * @param to End date for the history
     * @return ApiResponseDTO containing a list of manual parameters
     */
    @CircuitBreaker(name = MANUAL_CB, fallbackMethod = "fallbackGetManualParametersHistory")
    @Retry(name = MANUAL_CB)
    public ApiResponseDTO<List<ManualParameterDTO>> getManualParametersHistory(Long aquariumId, String from, String to) {
        return manualParametersRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/manual-parameters/aquarium/{aquariumId}/history")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build(aquariumId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // ========================
    // Target Parameters
    // ========================

    /**
     * Retrieves target parameters for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @return ApiResponseDTO containing the target parameters
     */
    @CircuitBreaker(name = TARGET_CB, fallbackMethod = "fallbackGetTargetParameters")
    @Retry(name = TARGET_CB)
    public ApiResponseDTO<TargetParameterDTO> getTargetParameters(Long aquariumId) {
        return targetParametersRestClient.get()
                .uri("/target-parameters/aquarium/{aquariumId}", aquariumId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Saves target parameters for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param targetParameter Target parameter to be saved
     * @return ApiResponseDTO containing the saved target parameter
     */
    @CircuitBreaker(name = TARGET_CB, fallbackMethod = "fallbackSaveTargetParameters")
    @Retry(name = TARGET_CB)
    public ApiResponseDTO<TargetParameterDTO> saveTargetParameters(Long aquariumId, TargetParameterDTO targetParameter) {
        return targetParametersRestClient.post()
                .uri("/target-parameters/aquarium/{aquariumId}", aquariumId)
                .body(targetParameter)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // ========================
    // Fallback Methods
    // ========================

    private ApiResponseDTO<WaterParameterDTO> fallbackAddWaterParameter(WaterParameterDTO parameter, Throwable t) {
        log.error("Circuit breaker fallback: failed to add water parameter", t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);
    }

    private ApiResponseDTO<List<WaterParameterDTO>> fallbackGetWaterParametersByAquarium(Long aquariumId, Integer limit, Throwable t) {
        log.error("Circuit breaker fallback: failed to get water parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", List.of(), null);
    }

    private ApiResponseDTO<WaterParameterDTO> fallbackGetLatestWaterParameter(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get latest water parameter for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);
    }

    private ApiResponseDTO<List<WaterParameterDTO>> fallbackGetWaterParametersHistory(Long aquariumId, String period, String from, String to, Throwable t) {
        log.error("Circuit breaker fallback: failed to get water parameters history for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", List.of(), null);
    }

    private ApiResponseDTO<ManualParameterDTO> fallbackAddManualParameter(ManualParameterDTO parameter, Throwable t) {
        log.error("Circuit breaker fallback: failed to add manual parameter", t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", null, null);
    }

    private ApiResponseDTO<ManualParameterDTO> fallbackGetLatestManualParameter(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get latest manual parameter for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", null, null);
    }

    private ApiResponseDTO<List<ManualParameterDTO>> fallbackGetAllManualParameters(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get manual parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", List.of(), null);
    }

    private ApiResponseDTO<List<ManualParameterDTO>> fallbackGetManualParametersHistory(Long aquariumId, String from, String to, Throwable t) {
        log.error("Circuit breaker fallback: failed to get manual parameters history for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", List.of(), null);
    }

    private ApiResponseDTO<TargetParameterDTO> fallbackGetTargetParameters(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get target parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Target parameters service unavailable", null, null);
    }

    private ApiResponseDTO<TargetParameterDTO> fallbackSaveTargetParameters(Long aquariumId, TargetParameterDTO targetParameter, Throwable t) {
        log.error("Circuit breaker fallback: failed to save target parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Target parameters service unavailable", null, null);
    }
}
