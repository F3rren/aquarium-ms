package it.f3rren.aquarium.manual_parameters_service.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.manual_parameters_service.Exception.ResourceNotFoundException;
import it.f3rren.aquarium.manual_parameters_service.Model.ManualParameter;
import it.f3rren.aquarium.manual_parameters_service.Repository.IManualParameterRepository;


@Service
public class ManualParameterService {
    
    @Autowired
    private IManualParameterRepository manualParameterRepository;
    
    public ManualParameter saveManualParameter(Long aquariumId, ManualParameter parameter) {
        parameter.setAquariumId(aquariumId);
        return manualParameterRepository.save(parameter);
    }
    
    public ManualParameter getLatestManualParameter(Long aquariumId) {
        ManualParameter parameter = manualParameterRepository.findFirstByAquariumIdOrderByMeasuredAtDesc(aquariumId);
        if (parameter == null) {
            throw new ResourceNotFoundException("No manual parameter found for aquarium with ID: " + aquariumId);
        }
        return parameter;
    }
    
    public List<ManualParameter> getAllManualParameters(Long aquariumId) {
        return manualParameterRepository.findByAquariumIdOrderByMeasuredAtDesc(aquariumId);
    }
    
    public List<ManualParameter> getManualParametersHistory(Long aquariumId, LocalDateTime from, LocalDateTime to) {
        return manualParameterRepository.findByAquariumIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            aquariumId, from, to
        );
    }
}
