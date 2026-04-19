package it.f3rren.aquarium.parameters_service.service;

import java.time.LocalDateTime;
import java.util.List;

import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.dto.ParameterDTO;

public interface IParameterService {

    ParameterDTO saveParameter(Long aquariumId, CreateParameterDTO dto);

    List<ParameterDTO> getParametersByAquariumId(Long aquariumId, Integer limit);

    ParameterDTO getLatestParameter(Long aquariumId);

    List<ParameterDTO> getParametersByPeriod(Long aquariumId, String period);

    List<ParameterDTO> getParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to);
}
