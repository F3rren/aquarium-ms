package it.f3rren.aquarium.maintenance_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private ProductCategory category;
    private String brand;
    private Double quantity;
    private String unit;
    private Double cost;
    private String currency;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String notes;
    private String imageUrl;
    private Boolean isFavorite;
    private Integer usageFrequency;
    private LocalDate lastUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean expired;
    private boolean expiringSoon;
    private boolean lowStock;
    private Long daysSinceLastUse;
    private boolean shouldUseAgain;
}
