package it.f3rren.aquarium.species.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.species.Model.Coral;
import it.f3rren.aquarium.species.Repository.ICoralRepository;
import it.f3rren.aquarium.species.Exception.ResourceNotFoundException;

@Service
public class CoralService {

    @Autowired
    private ICoralRepository coralRepository;

    public List<Coral> getAllCorals() {
        return coralRepository.findAllSortedByName();
    }

    public Coral getCoralById(Long id) {
        return coralRepository.findById(id.intValue()).orElseThrow(() -> new ResourceNotFoundException("Coral not found with ID: " + id));
    }
}
