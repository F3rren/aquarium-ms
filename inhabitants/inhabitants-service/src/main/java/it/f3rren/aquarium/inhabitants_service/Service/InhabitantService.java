package it.f3rren.aquarium.inhabitants_service.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.inhabitants_service.DTO.CoralDTO;
import it.f3rren.aquarium.inhabitants_service.DTO.FishDTO;
import it.f3rren.aquarium.inhabitants_service.DTO.InhabitantDetailsDTO;
import it.f3rren.aquarium.inhabitants_service.Exception.ResourceNotFoundException;
import it.f3rren.aquarium.inhabitants_service.Model.Inhabitant;
import it.f3rren.aquarium.inhabitants_service.Repository.IInhabitantRepository;

@Service
public class InhabitantService {
    
    @Autowired
    private IInhabitantRepository inhabitantRepository;
    
    @Autowired
    private FishService fishService;
    
    @Autowired
    private CoralService coralService;
    
    public List<InhabitantDetailsDTO> getInhabitantsByAquariumId(Long aquariumId) {
        List<Inhabitant> relations = inhabitantRepository.findByAquariumId(aquariumId);
        List<InhabitantDetailsDTO> result = new ArrayList<>();
        
        for (Inhabitant relation : relations) {
            InhabitantDetailsDTO dto = new InhabitantDetailsDTO();
            dto.setId(relation.getId());
            dto.setType(relation.getInhabitantType());
            dto.setQuantity(relation.getQuantity());
            dto.setAddedDate(relation.getAddedDate());
            
            try {
                if ("fish".equals(relation.getInhabitantType())) {
                    FishDTO fish = fishService.getFishById(relation.getInhabitantId());
                    dto.setCommonName(fish.getCommonName());
                    dto.setScientificName(fish.getScientificName());
                    dto.setDetails(fish);
                } else if ("coral".equals(relation.getInhabitantType())) {
                    CoralDTO coral = coralService.getCoralById(relation.getInhabitantId());
                    dto.setCommonName(coral.getCommonName());
                    dto.setScientificName(coral.getScientificName());
                    dto.setDetails(coral);
                }
            } catch (ResourceNotFoundException e) {
                // If the inhabitant is not found (e.g., deleted), we ignore it or set details to null
                // In this case we leave the details null but include the relation
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    public Inhabitant addInhabitant(Inhabitant inhabitant) {
        // Validation
        if (inhabitant.getInhabitantType() == null || inhabitant.getInhabitantId() == null) {
            throw new IllegalArgumentException("inhabitantType and inhabitantId are required");
        }
        
        // Verify that the fish or coral exists
        if ("fish".equals(inhabitant.getInhabitantType())) {
            fishService.getFishById(inhabitant.getInhabitantId());
        } else if ("coral".equals(inhabitant.getInhabitantType())) {
            coralService.getCoralById(inhabitant.getInhabitantId());
        } else {
            throw new IllegalArgumentException("inhabitantType must be 'fish' or 'coral'");
        }
        
        // Set quantity to 1 if null
        if (inhabitant.getQuantity() == null) {
            inhabitant.setQuantity(1);
        }
        
        return inhabitantRepository.save(inhabitant);
    }
    
    public void removeInhabitant(Long id) {
        if (!inhabitantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inhabitant not found with ID: " + id);
        }
        inhabitantRepository.deleteById(id);
    }
}
