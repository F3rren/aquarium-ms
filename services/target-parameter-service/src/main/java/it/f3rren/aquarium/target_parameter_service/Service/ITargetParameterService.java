package it.f3rren.aquarium.target_parameter_service.service;

import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.model.TargetParameter;

public interface ITargetParameterService {

    TargetParameter getTargetParameters(Long aquariumId);

    TargetParameter saveTargetParameters(Long aquariumId, SaveTargetParameterDTO dto);
}
