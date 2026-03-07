package it.f3rren.aquarium.aquariums_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.TargetParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.WaterParameterDTO;

import java.util.List;

/**
 * Client class to interact with the parameters microservices.
 * Uses Spring RestClient (synchronous) to make HTTP requests to water parameters,
 * manual parameters, and target parameters microservices.
 * @author F3rren
 */
@Component
public class ParametersClient {

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
    public ApiResponseDTO<TargetParameterDTO> saveTargetParameters(Long aquariumId, TargetParameterDTO targetParameter) {
        return targetParametersRestClient.post()
                .uri("/target-parameters/aquarium/{aquariumId}", aquariumId)
                .body(targetParameter)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
