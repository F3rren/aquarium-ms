package it.f3rren.aquarium.parameters_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.parameters_service.dto.CreateParameterDTO;
import it.f3rren.aquarium.parameters_service.dto.ParameterDTO;
import it.f3rren.aquarium.parameters_service.event.ParameterEventPublisher;
import it.f3rren.aquarium.parameters_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.parameters_service.model.Parameter;
import it.f3rren.aquarium.parameters_service.model.ParameterReadModel;
import it.f3rren.aquarium.parameters_service.repository.IParameterReadModelRepository;
import it.f3rren.aquarium.parameters_service.repository.IParameterRepository;

@Service
public class ParameterService implements IParameterService {

    private static final Logger log = LoggerFactory.getLogger(ParameterService.class);

    private final IParameterRepository parameterRepository;
    private final IParameterReadModelRepository readModelRepository;
    private final ParameterEventPublisher eventPublisher;

    public ParameterService(IParameterRepository parameterRepository,
                             IParameterReadModelRepository readModelRepository,
                             ParameterEventPublisher eventPublisher) {
        this.parameterRepository = parameterRepository;
        this.readModelRepository = readModelRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ParameterDTO saveParameter(Long aquariumId, CreateParameterDTO dto) {
        Parameter parameter = new Parameter();
        parameter.setAquariumId(aquariumId);
        parameter.setTemperature(dto.getTemperature());
        parameter.setPh(dto.getPh());
        parameter.setSalinity(dto.getSalinity());
        parameter.setOrp(dto.getOrp());

        log.info("Saving water parameter for aquarium {}", aquariumId);
        Parameter saved = parameterRepository.save(parameter);
        // CQRS write side: pubblica evento per aggiornare il read model in modo asincrono
        eventPublisher.publishParameterMeasured(saved);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ParameterDTO> getParametersByAquariumId(Long aquariumId, Integer limit) {
        List<Parameter> parameters = parameterRepository.findByAquariumIdOrderByMeasuredAtDesc(aquariumId);

        if (limit != null && limit > 0 && limit <= 50) {
            parameters = parameters.stream().limit(limit).toList();
        }

        return parameters.stream().map(this::toDTO).toList();
    }

    /**
     * CQRS read side: legge da parameter_latest invece di MAX(measured_at) sul write store.
     * Fallback sul write store se il read model non è ancora stato popolato.
     */
    @Transactional(readOnly = true)
    public ParameterDTO getLatestParameter(Long aquariumId) {
        return readModelRepository.findByAquariumId(aquariumId)
                .map(this::fromReadModel)
                .orElseGet(() -> toDTO(
                        parameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(aquariumId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "No parameter found for aquarium with ID: " + aquariumId))));
    }

    @Transactional(readOnly = true)
    public List<ParameterDTO> getParametersByPeriod(Long aquariumId, String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = switch (period) {
            case "week"  -> end.minusWeeks(1);
            case "month" -> end.minusMonths(1);
            default      -> end.minusDays(1);
        };

        return parameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(aquariumId, start, end)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ParameterDTO> getParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to) {
        return parameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(aquariumId, from, to)
                .stream().map(this::toDTO).toList();
    }

    private ParameterDTO toDTO(Parameter parameter) {
        ParameterDTO dto = new ParameterDTO();
        dto.setId(parameter.getId());
        dto.setAquariumId(parameter.getAquariumId());
        dto.setTemperature(parameter.getTemperature());
        dto.setPh(parameter.getPh());
        dto.setSalinity(parameter.getSalinity());
        dto.setOrp(parameter.getOrp());
        dto.setMeasuredAt(parameter.getMeasuredAt());
        return dto;
    }

    private ParameterDTO fromReadModel(ParameterReadModel model) {
        ParameterDTO dto = new ParameterDTO();
        dto.setAquariumId(model.getAquariumId());
        dto.setTemperature(model.getTemperature());
        dto.setPh(model.getPh());
        dto.setSalinity(model.getSalinity());
        dto.setOrp(model.getOrp());
        dto.setMeasuredAt(model.getMeasuredAt());
        return dto;
    }
}
