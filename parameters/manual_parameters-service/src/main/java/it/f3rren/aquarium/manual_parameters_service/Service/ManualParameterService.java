package it.f3rren.aquarium.manual_parameters_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.manual_parameters_service.dto.CreateManualParameterDTO;
import it.f3rren.aquarium.manual_parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.manual_parameters_service.model.ManualParameter;
import it.f3rren.aquarium.manual_parameters_service.repository.IManualParameterRepository;

@Service
public class ManualParameterService {

    private static final Logger log = LoggerFactory.getLogger(ManualParameterService.class);

    private final IManualParameterRepository manualParameterRepository;

    public ManualParameterService(IManualParameterRepository manualParameterRepository) {
        this.manualParameterRepository = manualParameterRepository;
    }

    @Transactional
    public ManualParameter saveManualParameter(Long aquariumId, CreateManualParameterDTO dto) {
        ManualParameter parameter = new ManualParameter();
        parameter.setAquariumId(aquariumId);
        parameter.setCalcium(dto.getCalcium());
        parameter.setMagnesium(dto.getMagnesium());
        parameter.setKh(dto.getKh());
        parameter.setNitrate(dto.getNitrate());
        parameter.setPhosphate(dto.getPhosphate());
        parameter.setNotes(dto.getNotes());

        log.info("Saving manual parameter for aquarium {}", aquariumId);
        return manualParameterRepository.save(parameter);
    }

    @Transactional(readOnly = true)
    public ManualParameter getLatestManualParameter(Long aquariumId) {
        ManualParameter parameter = manualParameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(aquariumId);
        if (parameter == null) {
            throw new ResourceNotFoundException("No manual parameter found for aquarium with ID: " + aquariumId);
        }
        return parameter;
    }

    @Transactional(readOnly = true)
    public List<ManualParameter> getAllManualParameters(Long aquariumId) {
        return manualParameterRepository.findByAquariumIdOrderByMeasuredAtDesc(aquariumId);
    }

    @Transactional(readOnly = true)
    public List<ManualParameter> getManualParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to) {
        return manualParameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                aquariumId, from, to);
    }
}
