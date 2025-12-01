package it.f3rren.aquarium.target_parameter_service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.target_parameter_service.Model.TargetParameter;
import it.f3rren.aquarium.target_parameter_service.Repository.ITargetParameterRepository;

@Service
public class TargetParameterService {
    
    @Autowired
    private ITargetParameterRepository targetParameterRepository;
    
    public TargetParameter getTargetParameters(Long aquariumId) {
        return targetParameterRepository.findByAquariumId(aquariumId);
    }
    
    public TargetParameter saveTargetParameters(Long aquariumId, TargetParameter targetParameter) {
        TargetParameter existing = targetParameterRepository.findByAquariumId(aquariumId);
        
        if (existing != null) {
            // Aggiorna i parametri esistenti
            existing.setTemperature(targetParameter.getTemperature());
            existing.setPh(targetParameter.getPh());
            existing.setSalinity(targetParameter.getSalinity());
            existing.setOrp(targetParameter.getOrp());
            existing.setCalcium(targetParameter.getCalcium());
            existing.setMagnesium(targetParameter.getMagnesium());
            existing.setKh(targetParameter.getKh());
            existing.setNitrate(targetParameter.getNitrate());
            existing.setPhosphate(targetParameter.getPhosphate());
            return targetParameterRepository.save(existing);
        } else {
            // Crea nuovi parametri target
            targetParameter.setAquariumId(aquariumId);
            return targetParameterRepository.save(targetParameter);
        }
    }
}
