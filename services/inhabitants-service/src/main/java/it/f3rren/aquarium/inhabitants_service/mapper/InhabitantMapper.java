package it.f3rren.aquarium.inhabitants_service.mapper;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.inhabitants_service.dto.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.model.Inhabitant;

@Component
public class InhabitantMapper {

    public InhabitantDetailsDTO toDetailsDTO(Inhabitant inhabitant) {
        InhabitantDetailsDTO dto = new InhabitantDetailsDTO();
        dto.setId(inhabitant.getId());
        dto.setType(inhabitant.getInhabitantType());
        dto.setQuantity(inhabitant.getQuantity());
        dto.setAddedDate(inhabitant.getAddedDate());
        dto.setNotes(inhabitant.getNotes());
        dto.setCustomName(inhabitant.getCustomName());
        dto.setCurrentSize(inhabitant.getCurrentSize());
        dto.setCustomDifficulty(inhabitant.getCustomDifficulty());
        dto.setCustomTemperament(inhabitant.getCustomTemperament());
        dto.setCustomDiet(inhabitant.getCustomDiet());
        dto.setIsReefSafe(inhabitant.getIsReefSafe());
        dto.setCustomMinTankSize(inhabitant.getCustomMinTankSize());
        return dto;
    }
}
