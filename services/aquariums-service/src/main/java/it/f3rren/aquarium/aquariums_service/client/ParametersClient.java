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
 * HTTP client for inter-service communication with the parameters microservices.
 *
 * <p>Uses Spring {@link RestClient} (synchronous) to call the water-parameters,
 * manual-parameters, and target-parameters services. Each method is protected by
 * a Resilience4j {@link CircuitBreaker} and {@link Retry}, with a dedicated fallback
 * that returns a degraded-but-valid response instead of propagating the failure.</p>
 *
 * @author F3rren
 */
@Component
public class ParametersClient {

    private static final Logger log = LoggerFactory.getLogger(ParametersClient.class);

    /** Circuit-breaker/retry name for the water-parameters service. */
    private static final String WATER_CB = "waterParameters";

    /** Circuit-breaker/retry name for the manual-parameters service. */
    private static final String MANUAL_CB = "manualParameters";

    /** Circuit-breaker/retry name for the target-parameters service. */
    private static final String TARGET_CB = "targetParameters";

    private final RestClient waterParametersRestClient;
    private final RestClient manualParametersRestClient;
    private final RestClient targetParametersRestClient;

    /**
     * Constructs the client injecting one {@link RestClient} per downstream service.
     *
     * @param waterParametersRestClient  pre-configured client for the water-parameters service
     * @param manualParametersRestClient pre-configured client for the manual-parameters service
     * @param targetParametersRestClient pre-configured client for the target-parameters service
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
     * Sends a new water parameter measurement to the water-parameters service.
     *
     * <p>The {@code aquariumId} from the path variable is set on the DTO here, so the
     * caller (controller) never needs to mutate the incoming request body.</p>
     *
     * @param aquariumId ID of the aquarium the measurement belongs to
     * @param parameter  water parameter data received from the client
     * @return wrapped response from the downstream service
     */
    @CircuitBreaker(name = WATER_CB, fallbackMethod = "fallbackAddWaterParameter")
    @Retry(name = WATER_CB)
    public ApiResponseDTO<WaterParameterDTO> addWaterParameter(Long aquariumId, WaterParameterDTO parameter) {
        parameter.setAquariumId(aquariumId);
        return waterParametersRestClient.post()
                .uri("/water-parameters")
                .body(parameter)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves the most recent {@code limit} water parameter measurements for an aquarium.
     *
     * @param aquariumId ID of the aquarium
     * @param limit      maximum number of records to return (1–100)
     * @return wrapped list of water parameters
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
     * Retrieves the single most recent water parameter measurement for an aquarium.
     *
     * @param aquariumId ID of the aquarium
     * @return wrapped latest water parameter
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
     * Retrieves water parameter history for an aquarium, filtered by period or date range.
     *
     * @param aquariumId ID of the aquarium
     * @param period     optional named period ("day", "week", "month"); mutually exclusive with from/to
     * @param from       optional ISO-8601 start date
     * @param to         optional ISO-8601 end date
     * @return wrapped list of historical water parameters
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
     * Sends a new manual parameter measurement to the manual-parameters service.
     *
     * <p>The {@code aquariumId} from the path variable is set on the DTO here, so the
     * caller (controller) never needs to mutate the incoming request body.</p>
     *
     * @param aquariumId ID of the aquarium the measurement belongs to
     * @param parameter  manual parameter data received from the client
     * @return wrapped response from the downstream service
     */
    @CircuitBreaker(name = MANUAL_CB, fallbackMethod = "fallbackAddManualParameter")
    @Retry(name = MANUAL_CB)
    public ApiResponseDTO<ManualParameterDTO> addManualParameter(Long aquariumId, ManualParameterDTO parameter) {
        parameter.setAquariumId(aquariumId);
        return manualParametersRestClient.post()
                .uri("/manual-parameters")
                .body(parameter)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * Retrieves the single most recent manual parameter measurement for an aquarium.
     *
     * @param aquariumId ID of the aquarium
     * @return wrapped latest manual parameter
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
     * Retrieves all manual parameter measurements for an aquarium.
     *
     * @param aquariumId ID of the aquarium
     * @return wrapped list of manual parameters
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
     * Retrieves manual parameter history for an aquarium within a date range.
     *
     * @param aquariumId ID of the aquarium
     * @param from       ISO-8601 start date (inclusive)
     * @param to         ISO-8601 end date (inclusive)
     * @return wrapped list of historical manual parameters
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
     * Retrieves the target (ideal) parameters configured for an aquarium.
     *
     * @param aquariumId ID of the aquarium
     * @return wrapped target parameters
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
     * Saves (creates or replaces) the target parameters for an aquarium.
     *
     * @param aquariumId      ID of the aquarium
     * @param targetParameter target parameter values to persist
     * @return wrapped saved target parameters
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

    /**
     * Fallback for {@link #addWaterParameter} when the water-parameters service is unavailable.
     *
     * @param aquariumId ID of the aquarium (unused in fallback, required by Resilience4j signature)
     * @param parameter  original request DTO (unused in fallback, required by Resilience4j signature)
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<WaterParameterDTO> fallbackAddWaterParameter(Long aquariumId, WaterParameterDTO parameter, Throwable t) {
        log.error("Circuit breaker fallback: failed to add water parameter for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #getWaterParametersByAquarium} when the service is unavailable.
     *
     * @param aquariumId ID of the aquarium
     * @param limit      original limit parameter
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<List<WaterParameterDTO>> fallbackGetWaterParametersByAquarium(Long aquariumId, Integer limit, Throwable t) {
        log.error("Circuit breaker fallback: failed to get water parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #getLatestWaterParameter} when the service is unavailable.
     *
     * @param aquariumId ID of the aquarium
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<WaterParameterDTO> fallbackGetLatestWaterParameter(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get latest water parameter for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #getWaterParametersHistory} when the service is unavailable.
     *
     * @param aquariumId ID of the aquarium
     * @param period     original period filter
     * @param from       original from-date filter
     * @param to         original to-date filter
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<List<WaterParameterDTO>> fallbackGetWaterParametersHistory(Long aquariumId, String period, String from, String to, Throwable t) {
        log.error("Circuit breaker fallback: failed to get water parameters history for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Water parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #addManualParameter} when the manual-parameters service is unavailable.
     *
     * @param aquariumId ID of the aquarium (unused in fallback, required by Resilience4j signature)
     * @param parameter  original request DTO (unused in fallback, required by Resilience4j signature)
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<ManualParameterDTO> fallbackAddManualParameter(Long aquariumId, ManualParameterDTO parameter, Throwable t) {
        log.error("Circuit breaker fallback: failed to add manual parameter for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #getLatestManualParameter} when the service is unavailable.
     *
     * @param aquariumId ID of the aquarium
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<ManualParameterDTO> fallbackGetLatestManualParameter(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get latest manual parameter for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #getAllManualParameters} when the service is unavailable.
     *
     * @param aquariumId ID of the aquarium
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<List<ManualParameterDTO>> fallbackGetAllManualParameters(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get manual parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #getManualParametersHistory} when the service is unavailable.
     *
     * @param aquariumId ID of the aquarium
     * @param from       original from-date filter
     * @param to         original to-date filter
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<List<ManualParameterDTO>> fallbackGetManualParametersHistory(Long aquariumId, String from, String to, Throwable t) {
        log.error("Circuit breaker fallback: failed to get manual parameters history for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Manual parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #getTargetParameters} when the target-parameters service is unavailable.
     *
     * @param aquariumId ID of the aquarium
     * @param t          the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<TargetParameterDTO> fallbackGetTargetParameters(Long aquariumId, Throwable t) {
        log.error("Circuit breaker fallback: failed to get target parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Target parameters service unavailable", null, null);
    }

    /**
     * Fallback for {@link #saveTargetParameters} when the target-parameters service is unavailable.
     *
     * @param aquariumId      ID of the aquarium
     * @param targetParameter original request DTO (unused in fallback, required by Resilience4j signature)
     * @param t               the exception that triggered the fallback
     * @return degraded response with {@code success=false}
     */
    private ApiResponseDTO<TargetParameterDTO> fallbackSaveTargetParameters(Long aquariumId, TargetParameterDTO targetParameter, Throwable t) {
        log.error("Circuit breaker fallback: failed to save target parameters for aquarium {}", aquariumId, t);
        return new ApiResponseDTO<>(false, "Target parameters service unavailable", null, null);
    }
}
