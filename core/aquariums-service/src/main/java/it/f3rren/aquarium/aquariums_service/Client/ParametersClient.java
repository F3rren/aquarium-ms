package it.f3rren.aquarium.aquariums_service.Client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import it.f3rren.aquarium.aquariums_service.DTO.ApiResponseDTO;
import it.f3rren.aquarium.aquariums_service.DTO.WaterParameterDTO;
import it.f3rren.aquarium.aquariums_service.DTO.ManualParameterDTO;
import it.f3rren.aquarium.aquariums_service.DTO.TargetParameterDTO;

import java.util.List;

@Component
public class ParametersClient {

    private final WebClient waterParametersWebClient;
    private final WebClient manualParametersWebClient;
    private final WebClient targetParametersWebClient;

    public ParametersClient(
            @Qualifier("waterParametersWebClient") WebClient waterParametersWebClient,
            @Qualifier("manualParametersWebClient") WebClient manualParametersWebClient,
            @Qualifier("targetParametersWebClient") WebClient targetParametersWebClient) {
        this.waterParametersWebClient = waterParametersWebClient;
        this.manualParametersWebClient = manualParametersWebClient;
        this.targetParametersWebClient = targetParametersWebClient;
    }

    // Water Parameters
    public ApiResponseDTO<WaterParameterDTO> addWaterParameter(WaterParameterDTO parameter) {
        return waterParametersWebClient.post()
                .uri("/water-parameters")
                .bodyValue(parameter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<WaterParameterDTO>>() {})
                .block();
    }

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

    public ApiResponseDTO<WaterParameterDTO> getLatestWaterParameter(Long aquariumId) {
        return waterParametersWebClient.get()
                .uri("/water-parameters/aquarium/{aquariumId}/latest", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<WaterParameterDTO>>() {})
                .block();
    }

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

    // Manual Parameters
    public ApiResponseDTO<ManualParameterDTO> addManualParameter(ManualParameterDTO parameter) {
        return manualParametersWebClient.post()
                .uri("/manual-parameters")
                .bodyValue(parameter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<ManualParameterDTO>>() {})
                .block();
    }

    public ApiResponseDTO<ManualParameterDTO> getLatestManualParameter(Long aquariumId) {
        return manualParametersWebClient.get()
                .uri("/manual-parameters/aquarium/{aquariumId}/latest", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<ManualParameterDTO>>() {})
                .block();
    }

    public ApiResponseDTO<List<ManualParameterDTO>> getAllManualParameters(Long aquariumId) {
        return manualParametersWebClient.get()
                .uri("/manual-parameters/aquarium/{aquariumId}", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<List<ManualParameterDTO>>>() {})
                .block();
    }

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

    // Target Parameters
    public ApiResponseDTO<TargetParameterDTO> getTargetParameters(Long aquariumId) {
        return targetParametersWebClient.get()
                .uri("/target-parameters/aquarium/{aquariumId}", aquariumId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<TargetParameterDTO>>() {})
                .block();
    }

    public ApiResponseDTO<TargetParameterDTO> saveTargetParameters(Long aquariumId, TargetParameterDTO targetParameter) {
        return targetParametersWebClient.post()
                .uri("/target-parameters/aquarium/{aquariumId}", aquariumId)
                .bodyValue(targetParameter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<TargetParameterDTO>>() {})
                .block();
    }
}
