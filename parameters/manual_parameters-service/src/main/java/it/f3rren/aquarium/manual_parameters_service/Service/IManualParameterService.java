package it.f3rren.aquarium.manual_parameters_service.service;

import java.time.LocalDateTime;
import java.util.List;

import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.model.ManualParameter;

public interface IManualParameterService {

    ManualParameter saveManualParameter(Long aquariumId, CreateManualParameterDTO dto);

    ManualParameter getLatestManualParameter(Long aquariumId);

    List<ManualParameter> getAllManualParameters(Long aquariumId);

    List<ManualParameter> getManualParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to);
}
