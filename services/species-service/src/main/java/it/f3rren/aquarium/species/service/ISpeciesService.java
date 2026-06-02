package it.f3rren.aquarium.species.service;

import java.util.List;

import it.f3rren.aquarium.species.dto.CoralResponseDTO;
import it.f3rren.aquarium.species.dto.FishResponseDTO;

public interface ISpeciesService {

    List<FishResponseDTO> getAllFish();

    FishResponseDTO getFishById(Long id);

    List<CoralResponseDTO> getAllCorals();

    CoralResponseDTO getCoralById(Long id);
}
