package it.f3rren.aquarium.manual_parameters_service.service;

import java.time.LocalDateTime;
import java.util.List;

import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.dto.ManualParameterDTO;

public interface IManualParameterService {

    ManualParameterDTO saveManualParameter(Long aquariumId, CreateManualParameterDTO dto);

    ManualParameterDTO getLatestManualParameter(Long aquariumId);

    List<ManualParameterDTO> getAllManualParameters(Long aquariumId);

    List<ManualParameterDTO> getManualParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to);
}
