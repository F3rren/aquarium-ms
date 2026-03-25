package it.f3rren.aquarium.maintenance_service.service;

import java.util.List;

import it.f3rren.aquarium.maintenance_service.dto.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;

public interface IProductService {

    Product createProduct(CreateProductDTO dto);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    List<Product> getProductsByCategory(ProductCategory category);

    List<Product> getFavoriteProducts();

    List<Product> getProductsByBrand(String brand);

    List<Product> getExpiredProducts();

    List<Product> getProductsExpiringSoon();

    List<Product> getLowStockProducts();

    List<Product> searchProductsByName(String name);

    List<Product> getProductsToUseAgain();

    Product updateProduct(Long id, UpdateProductDTO dto);

    Product markAsUsed(Long id);

    Product toggleFavorite(Long id);

    Product updateQuantity(Long id, Double quantityChange);

    void deleteProduct(Long id);
}
