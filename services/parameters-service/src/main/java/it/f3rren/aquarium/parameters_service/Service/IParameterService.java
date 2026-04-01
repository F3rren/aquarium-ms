package it.f3rren.aquarium.parameters_service.service;

import java.time.LocalDateTime;
import java.util.List;

import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.model.Parameter;

public interface IParameterService {

    Parameter saveParameter(Long aquariumId, CreateParameterDTO dto);

    List<Parameter> getParametersByAquariumId(Long aquariumId, Integer limit);

    Parameter getLatestParameter(Long aquariumId);

    List<Parameter> getParametersByPeriod(Long aquariumId, String period);

    List<Parameter> getParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to);
}
