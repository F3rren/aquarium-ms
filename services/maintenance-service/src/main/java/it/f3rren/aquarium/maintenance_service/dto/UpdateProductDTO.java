package it.f3rren.aquarium.maintenance_service.dto;

import java.time.LocalDate;

import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductDTO {

    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    private ProductCategory category;

    @Size(max = 255, message = "Brand must be at most 255 characters")
    private String brand;

    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @Size(max = 50, message = "Unit must be at most 50 characters")
    private String unit;

    @Positive(message = "Cost must be positive")
    private Double cost;

    @Size(max = 10, message = "Currency must be at most 10 characters")
    private String currency;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    @Size(max = 2000, message = "Notes must be at most 2000 characters")
    private String notes;

    @Size(max = 500, message = "Image URL must be at most 500 characters")
    private String imageUrl;

    private Boolean isFavorite;

    @Positive(message = "Usage frequency must be positive")
    private Integer usageFrequency;

    private LocalDate lastUsed;
}
