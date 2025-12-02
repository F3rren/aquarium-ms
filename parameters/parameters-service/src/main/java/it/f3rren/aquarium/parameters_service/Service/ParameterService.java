package it.f3rren.aquarium.parameters_service.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.parameters_service.Exception.ResourceNotFoundException;
import it.f3rren.aquarium.parameters_service.Model.Parameter;
import it.f3rren.aquarium.parameters_service.Repository.IParameterRepository;

@Service
public class ParameterService {
    
    @Autowired
    private IParameterRepository parameterRepository;
    
    public Parameter saveParameter(Long aquariumId, Parameter parameter) {
        parameter.setAquariumId(aquariumId);
        return parameterRepository.save(parameter);
    }
    
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
    
    public Parameter getLatestParameter(Long aquariumId) {
        Parameter parameter = parameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(aquariumId);
        if (parameter == null) {
            throw new ResourceNotFoundException("No parameter found for aquarium with ID: " + aquariumId);
        }
        return parameter;
    }
    
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
            aquariumId, start, end
        );
    }
    
    public List<Parameter> getParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to) {
        return parameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            aquariumId, from, to
        );
    }
}
