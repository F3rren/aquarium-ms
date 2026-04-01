package it.f3rren.aquarium.target_parameter_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.target_parameter_service.dto.SaveTargetParameterDTO;
import it.f3rren.aquarium.target_parameter_service.model.TargetParameter;
import it.f3rren.aquarium.target_parameter_service.repository.ITargetParameterRepository;

@Service
public class TargetParameterService implements ITargetParameterService {

    private static final Logger log = LoggerFactory.getLogger(TargetParameterService.class);

    private final ITargetParameterRepository targetParameterRepository;

    public TargetParameterService(ITargetParameterRepository targetParameterRepository) {
        this.targetParameterRepository = targetParameterRepository;
    }

    @Transactional(readOnly = true)
    public TargetParameter getTargetParameters(Long aquariumId) {
        log.info("Recupero target parameters per acquario id={}", aquariumId);
        return targetParameterRepository.findByAquariumId(aquariumId);
    }

    @Transactional
    public TargetParameter saveTargetParameters(Long aquariumId, SaveTargetParameterDTO dto) {
        TargetParameter existing = targetParameterRepository.findByAquariumId(aquariumId);

        if (existing != null) {
            log.info("Aggiornamento target parameters per acquario id={}", aquariumId);
            existing.setTemperature(dto.getTemperature());
            existing.setPh(dto.getPh());
            existing.setSalinity(dto.getSalinity());
            existing.setOrp(dto.getOrp());
            return targetParameterRepository.save(existing);
        } else {
            log.info("Creazione target parameters per acquario id={}", aquariumId);
            TargetParameter target = new TargetParameter();
            target.setAquariumId(aquariumId);
            target.setTemperature(dto.getTemperature());
            target.setPh(dto.getPh());
            target.setSalinity(dto.getSalinity());
            target.setOrp(dto.getOrp());
            return targetParameterRepository.save(target);
        }
    }
}
