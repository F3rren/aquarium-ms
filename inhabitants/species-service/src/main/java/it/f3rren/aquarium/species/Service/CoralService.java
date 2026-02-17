package it.f3rren.aquarium.species.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.species.exception.ResourceNotFoundException;
import it.f3rren.aquarium.species.model.Coral;
import it.f3rren.aquarium.species.repository.ICoralRepository;

@Service
public class CoralService {

    private static final Logger log = LoggerFactory.getLogger(CoralService.class);

    private final ICoralRepository coralRepository;

    public CoralService(ICoralRepository coralRepository) {
        this.coralRepository = coralRepository;
    }

    @Transactional(readOnly = true)
    public List<Coral> getAllCorals() {
        log.debug("Retrieving all coral species");
        return coralRepository.findAllSortedByName();
    }

    @Transactional(readOnly = true)
    public Coral getCoralById(Long id) {
        return coralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coral not found with ID: " + id));
    }
}
