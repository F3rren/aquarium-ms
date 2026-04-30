package it.f3rren.aquarium.maintenance_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import it.f3rren.aquarium.maintenance_service.dto.response.ProductDTO;
import it.f3rren.aquarium.maintenance_service.model.Product;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setQuantity(product.getQuantity());
        dto.setUnit(product.getUnit());
        dto.setCost(product.getCost());
        dto.setCurrency(product.getCurrency());
        dto.setPurchaseDate(product.getPurchaseDate());
        dto.setExpiryDate(product.getExpiryDate());
        dto.setNotes(product.getNotes());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsFavorite(product.getIsFavorite());
        dto.setUsageFrequency(product.getUsageFrequency());
        dto.setLastUsed(product.getLastUsed());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setExpired(product.isExpired());
        dto.setExpiringSoon(product.isExpiringSoon());
        dto.setLowStock(product.isLowStock());
        dto.setDaysSinceLastUse(product.getDaysSinceLastUse());
        dto.setShouldUseAgain(product.shouldUseAgain());
        return dto;
    }

    public List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream().map(this::toDTO).toList();
    }
}
