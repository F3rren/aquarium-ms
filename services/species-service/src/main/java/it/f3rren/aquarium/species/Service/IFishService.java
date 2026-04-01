package it.f3rren.aquarium.species.service;

import java.util.List;

import it.f3rren.aquarium.species.model.Fish;

public interface IFishService {

    List<Fish> getAllFish();

    Fish getFishById(Long id);
}
