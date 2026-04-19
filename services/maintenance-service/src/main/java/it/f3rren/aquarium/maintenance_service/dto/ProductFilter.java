package it.f3rren.aquarium.maintenance_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String category;
    private String brand;
    private String search;
    private Boolean favorites;
    private Boolean expired;
    private Boolean expiringSoon;
    private Boolean lowStock;
    private Boolean shouldUseAgain;
}
