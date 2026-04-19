package it.f3rren.aquarium.maintenance_service.service;

import java.util.List;

import it.f3rren.aquarium.maintenance_service.dto.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.ProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.ProductFilter;
import it.f3rren.aquarium.maintenance_service.dto.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;

public interface IProductService {

    ProductDTO createProduct(CreateProductDTO dto);

    List<ProductDTO> getProducts(ProductFilter filter);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getProductsByCategory(ProductCategory category);

    ProductDTO updateProduct(Long id, UpdateProductDTO dto);

    ProductDTO markAsUsed(Long id);

    ProductDTO toggleFavorite(Long id);

    ProductDTO updateQuantity(Long id, Double quantityChange);

    void deleteProduct(Long id);
}
