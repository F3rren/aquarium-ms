package it.f3rren.aquarium.parameters_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.parameters_service.model.Parameter;
import it.f3rren.aquarium.parameters_service.repository.IParameterRepository;

@Service
public class ParameterService {

    private static final Logger log = LoggerFactory.getLogger(ParameterService.class);

    private final IParameterRepository parameterRepository;

    public ParameterService(IParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Transactional
    public Parameter saveParameter(Long aquariumId, CreateParameterDTO dto) {
        Parameter parameter = new Parameter();
        parameter.setAquariumId(aquariumId);
        parameter.setTemperature(dto.getTemperature());
        parameter.setPh(dto.getPh());
        parameter.setSalinity(dto.getSalinity());
        parameter.setOrp(dto.getOrp());

        log.info("Saving water parameter for aquarium {}", aquariumId);
        return parameterRepository.save(parameter);
    }

    @Transactional(readOnly = true)
    public List<Parameter> getParametersByAquariumId(Long aquariumId, Integer limit) {
        List<Parameter> parameters;
        if (limit != null && limit <= 50) {
            parameters = parameterRepository.findByAquariumIdOrderByMeasuredAtDesc(aquariumId)
                    .stream()
                    .limit(limit)
                    .toList();
        } else {
            parameters = parameterRepository.findByAquariumIdOrderByMeasuredAtDesc(aquariumId);
        }

        if (parameters.isEmpty()) {
            throw new ResourceNotFoundException("No parameter found for aquarium with ID: " + aquariumId);
        }
        return parameters;
    }

    @Transactional(readOnly = true)
    public Parameter getLatestParameter(Long aquariumId) {
        Parameter parameter = parameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(aquariumId);
        if (parameter == null) {
            throw new ResourceNotFoundException("No parameter found for aquarium with ID: " + aquariumId);
        }
        return parameter;
    }

    @Transactional(readOnly = true)
    public List<Parameter> getParametersByPeriod(Long aquariumId, String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;

        switch (period != null ? period : "day") {
            case "week":
                start = end.minusWeeks(1);
                break;
            case "month":
                start = end.minusMonths(1);
                break;
            default:
                start = end.minusDays(1);
        }

        return parameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                aquariumId, start, end);
    }

    @Transactional(readOnly = true)
    public List<Parameter> getParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to) {
        return parameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                aquariumId, from, to);
    }
}
