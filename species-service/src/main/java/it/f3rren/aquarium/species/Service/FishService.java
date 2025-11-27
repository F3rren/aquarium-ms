package it.f3rren.aquarium.species.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.species.Model.Fish;
import it.f3rren.aquarium.species.Repository.IFishRepository;
import it.f3rren.aquarium.species.Exception.ResourceNotFoundException;

@Service
public class FishService {
    
    @Autowired
    private IFishRepository fishRepository;
    
    public List<Fish> getAllFishs() {
        return fishRepository.findAllSortedByName();
    }

    public Fish getFishById(Long id) {
        return fishRepository.findById(id.intValue())
            .orElseThrow(() -> new ResourceNotFoundException("Pesce non trovato con ID: " + id));
    }
}
