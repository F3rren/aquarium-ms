package it.f3rren.aquarium.aquariums_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.dto.ManualParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.TargetParameterDTO;
import it.f3rren.aquarium.aquariums_service.dto.WaterParameterDTO;

import java.util.List;

/**
 * Client class to interact with the parameters microservice.
 * It uses WebClient to make HTTP requests to the parameters microservice.
 * It provides methods to interact with the different endpoints related to water parameters, manual parameters, and target parameters.
 * The WebClient instances are injected as qualifiers to differentiate between different clients for water parameters, manual parameters, and target parameters.
 * The methods use the blocking WebClient API to make synchronous requests to the microservice endpoints.
 * @author F3rren
 */
@Component
public class ParametersClient {

    private final WebClient waterParametersWebClient;
    private final WebClient manualParametersWebClient;
    private final WebClient targetParametersWebClient;

    /**
     * Constructor for ParametersClient.
     * @param waterParametersWebClient WebClient for water parameters microservice
     * @param manualParametersWebClient WebClient for manual parameters microservice
     * @param targetParametersWebClient WebClient for target parameters microservice
     */
    public ParametersClient(
            @Qualifier("waterParametersWebClient") WebClient waterParametersWebClient,
            @Qualifier("manualParametersWebClient") WebClient manualParametersWebClient,
            @Qualifier("targetParametersWebClient") WebClient targetParametersWebClient) {
        this.waterParametersWebClient = waterParametersWebClient;
        this.manualParametersWebClient = manualParametersWebClient;
        this.targetParametersWebClient = targetParametersWebClient;
    }

    /**
     * Adds a new water parameter to the microservice.
     * @param parameter WaterParameterDTO object to be added
     * @return ApiResponseDTO containing the added water parameter
     */
    public ApiResponseDTO<WaterParameterDTO> addWaterParameter(WaterParameterDTO parameter) {
        return waterParametersWebClient.post()
                .uri("/water-parameters")
                .bodyValue(parameter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<WaterParameterDTO>>() {})
                .block();
    }

    /**
     * Retrieves water parameters for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param limit Maximum number of parameters to retrieve
     * @return ApiResponseDTO containing a list of water parameters
     */
    public ApiResponseDTO<List<WaterParameterDTO>> getWaterParametersByAquarium(Long aquariumId, Integer limit) {
        return waterParametersWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/water-parameters/aquarium/{aquariumId}")
                        .queryParam("limit", limit)
                        .build(aquariumId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<List<WaterParameterDTO>>>() {})
                .block();
    }

    /**
     * Retrieves the latest water parameter for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @return ApiResponseDTO containing the latest water parameter
     */
    public ApiResponseDTO<WaterParameterDTO> getLatestWaterParameter(Long aquariumId) {
        return waterParametersWebClient.get()
                .uri("/water-parameters/aquarium/{aquariumId}/latest", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<WaterParameterDTO>>() {})
                .block();
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
        return waterParametersWebClient.get()
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
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<List<WaterParameterDTO>>>() {})
                .block();
    }

    /**
     * Deletes a water parameter by its ID.
     * @param parameter ID of the manual parameter
     * @return ApiResponseDTO containing the deleted manual parameter
     */
    public ApiResponseDTO<ManualParameterDTO> addManualParameter(ManualParameterDTO parameter) {
        return manualParametersWebClient.post()
                .uri("/manual-parameters")
                .bodyValue(parameter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<ManualParameterDTO>>() {})
                .block();
    }

    /**
     * Retrieves the latest manual parameter for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @return ApiResponseDTO containing the latest manual parameter
     */
    public ApiResponseDTO<ManualParameterDTO> getLatestManualParameter(Long aquariumId) {
        return manualParametersWebClient.get()
                .uri("/manual-parameters/aquarium/{aquariumId}/latest", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<ManualParameterDTO>>() {})
                .block();
    }

    /**
     * Retrieves manual parameters history for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param from Start date for the history
     * @param to End date for the history
     * @return ApiResponseDTO containing a list of manual parameters
     */
    public ApiResponseDTO<List<ManualParameterDTO>> getAllManualParameters(Long aquariumId) {
        return manualParametersWebClient.get()
                .uri("/manual-parameters/aquarium/{aquariumId}", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<List<ManualParameterDTO>>>() {})
                .block();
    }

    /**
     * Retrieves manual parameters history for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param from Start date for the history
     * @param to End date for the history
     * @return ApiResponseDTO containing a list of manual parameters
     */
    public ApiResponseDTO<List<ManualParameterDTO>> getManualParametersHistory(Long aquariumId, String from, String to) {
        return manualParametersWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/manual-parameters/aquarium/{aquariumId}/history")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build(aquariumId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<List<ManualParameterDTO>>>() {})
                .block();
    }

    /**
     * Adds a new target parameter for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @return ApiResponseDTO containing the added target parameter
     */
    public ApiResponseDTO<TargetParameterDTO> getTargetParameters(Long aquariumId) {
        return targetParametersWebClient.get()
                .uri("/target-parameters/aquarium/{aquariumId}", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<TargetParameterDTO>>() {})
                .block();
    }

    /**
     * Saves target parameters for a specific aquarium.
     * @param aquariumId ID of the aquarium
     * @param targetParameter Target parameter to be saved
     * @return ApiResponseDTO containing the saved target parameter
     */
    public ApiResponseDTO<TargetParameterDTO> saveTargetParameters(Long aquariumId, TargetParameterDTO targetParameter) {
        return targetParametersWebClient.post()
                .uri("/target-parameters/aquarium/{aquariumId}", aquariumId)
                .bodyValue(targetParameter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<TargetParameterDTO>>() {})
                .block();
    }
}
