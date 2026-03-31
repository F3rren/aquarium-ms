package it.f3rren.aquarium.species.service;

import java.util.List;

import it.f3rren.aquarium.species.model.Coral;

public interface ICoralService {

    List<Coral> getAllCorals();

    Coral getCoralById(Long id);
}
