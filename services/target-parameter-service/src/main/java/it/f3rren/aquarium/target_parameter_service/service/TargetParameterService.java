package it.f3rren.aquarium.target_parameter_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.dto.TargetParameterResponseDTO;
import it.f3rren.aquarium.target_parameter_service.model.TargetParameter;
import it.f3rren.aquarium.target_parameter_service.repository.ITargetParameterRepository;

@Service
public class TargetParameterService implements ITargetParameterService {

    private static final Logger log = LoggerFactory.getLogger(TargetParameterService.class);

    private final ITargetParameterRepository targetParameterRepository;

    public TargetParameterService(ITargetParameterRepository targetParameterRepository) {
        this.targetParameterRepository = targetParameterRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public TargetParameterResponseDTO getTargetParameters(Long aquariumId) {
        log.info("Retrieving target parameters for aquarium id={}", aquariumId);
        return targetParameterRepository.findByAquariumId(aquariumId)
                .map(this::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public TargetParameterResponseDTO saveTargetParameters(Long aquariumId, SaveTargetParameterDTO dto) {
        TargetParameter target = targetParameterRepository.findByAquariumId(aquariumId)
                .orElseGet(() -> {
                    log.info("Creating target parameters for aquarium id={}", aquariumId);
                    TargetParameter t = new TargetParameter();
                    t.setAquariumId(aquariumId);
                    return t;
                });

        log.info("Saving target parameters for aquarium id={}", aquariumId);
        target.setTemperature(dto.getTemperature());
        target.setPh(dto.getPh());
        target.setSalinity(dto.getSalinity());
        target.setOrp(dto.getOrp());

        return toDTO(targetParameterRepository.save(target));
    }

    private TargetParameterResponseDTO toDTO(TargetParameter target) {
        return new TargetParameterResponseDTO(
                target.getId(),
                target.getAquariumId(),
                target.getTemperature(),
                target.getPh(),
                target.getSalinity(),
                target.getOrp());
    }
}
