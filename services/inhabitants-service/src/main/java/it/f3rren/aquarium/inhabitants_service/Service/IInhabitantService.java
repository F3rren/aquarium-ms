package it.f3rren.aquarium.inhabitants_service.service;

import java.util.List;

import it.f3rren.aquarium.inhabitants_service.dto.CreateInhabitantDTO;
import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.dto.UpdateInhabitantDTO;

public interface IInhabitantService {

    List<InhabitantDetailsDTO> getInhabitantsByAquariumId(Long aquariumId);

    InhabitantDetailsDTO addInhabitant(Long aquariumId, CreateInhabitantDTO dto);

    void removeInhabitant(Long aquariumId, Long inhabitantId);

    InhabitantDetailsDTO updateInhabitant(Long aquariumId, Long inhabitantId, UpdateInhabitantDTO dto);
}
