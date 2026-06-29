package it.f3rren.aquarium.target_parameter_service.service;

import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.dto.TargetParameterResponseDTO;

public interface ITargetParameterService {

    TargetParameterResponseDTO getTargetParameters(Long aquariumId);

    TargetParameterResponseDTO saveTargetParameters(Long aquariumId, SaveTargetParameterDTO dto);
}
