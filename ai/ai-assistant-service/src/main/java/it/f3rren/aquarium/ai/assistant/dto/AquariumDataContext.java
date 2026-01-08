package it.f3rren.aquarium.ai.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AquariumDataContext {
    private List<String> aquariums;
    private List<String> inhabitants;
    private List<String> parameters;
    private List<String> maintenance;
    
    public String toContextString() {
        StringBuilder context = new StringBuilder();
        
        if (aquariums != null && !aquariums.isEmpty()) {
            context.append("Acquari disponibili:\n");
            aquariums.forEach(a -> context.append("- ").append(a).append("\n"));
        }
        
        if (inhabitants != null && !inhabitants.isEmpty()) {
            context.append("\nAbitanti negli acquari:\n");
            inhabitants.forEach(i -> context.append("- ").append(i).append("\n"));
        }
        
        if (parameters != null && !parameters.isEmpty()) {
            context.append("\nParametri dell'acqua:\n");
            parameters.forEach(p -> context.append("- ").append(p).append("\n"));
        }
        
        if (maintenance != null && !maintenance.isEmpty()) {
            context.append("\nManutenzioni recenti:\n");
            maintenance.forEach(m -> context.append("- ").append(m).append("\n"));
        }
        
        return context.toString();
    }
}
