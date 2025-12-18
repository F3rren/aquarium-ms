package it.f3rren.aquarium.maintenance_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    
    // Find by category
    List<Product> findByCategoryOrderByNameAsc(ProductCategory category);
    
    // Find favorites
    List<Product> findByIsFavoriteTrueOrderByNameAsc();
    
    // Find by brand
    List<Product> findByBrandOrderByNameAsc(String brand);
    
    // Find expired products
    List<Product> findByExpiryDateBefore(LocalDate date);
    
    // Find products expiring soon (within next 30 days)
    List<Product> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find low stock products
    @Query("SELECT p FROM Product p WHERE p.quantity IS NOT NULL AND p.quantity < 20")
    List<Product> findLowStockProducts();
    
    // Search by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    
    // Find products that should be used again based on frequency
    @Query("SELECT p FROM Product p WHERE p.usageFrequency IS NOT NULL AND p.lastUsed IS NOT NULL " +
           "AND FUNCTION('DATEDIFF', CURRENT_DATE, p.lastUsed) >= p.usageFrequency")
    List<Product> findProductsToUseAgain();
}
