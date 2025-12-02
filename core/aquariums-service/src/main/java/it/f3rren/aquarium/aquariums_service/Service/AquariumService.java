package it.f3rren.aquarium.aquariums_service.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.aquariums_service.Model.Aquarium;
import it.f3rren.aquarium.aquariums_service.Repository.IAquariumRepository;
import it.f3rren.aquarium.aquariums_service.Exception.ResourceNotFoundException;

@Service
public class AquariumService {
    
    @Autowired
    private IAquariumRepository aquariumRepository;

    public Aquarium createAquarium(Aquarium aquarium) {
        return aquariumRepository.save(aquarium);
    }

    public List<Aquarium> getAllAquariums() {
        return aquariumRepository.findAll();
    }
    
    public Aquarium getAquariumById(Long id) {
        return aquariumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aquarium not found with ID: " + id));
    }

    public Aquarium updateAquarium(Long id, Aquarium aquarium) {
        // Verify that the aquarium exists
        if (!aquariumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aquarium not found with ID: " + id);
        }
        aquarium.setId(id);  // Set the ID for the update
        return aquariumRepository.save(aquarium);
    }
    
    public void deleteAquarium(Long id) {
        if (!aquariumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aquarium not found with ID: " + id);
        }
        aquariumRepository.deleteById(id);
    }
}