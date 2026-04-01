package it.f3rren.aquarium.inhabitants_service.service;

import java.util.List;

import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.model.Inhabitant;

public interface IInhabitantService {

    List<InhabitantDetailsDTO> getInhabitantsByAquariumId(Long aquariumId);

    Inhabitant addInhabitant(Long aquariumId, CreateInhabitantDTO dto);

    void removeInhabitant(Long aquariumId, Long inhabitantId);

    Inhabitant updateInhabitant(Long aquariumId, Long inhabitantId, UpdateInhabitantDTO dto);
}
