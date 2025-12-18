package it.f3rren.aquarium.maintenance_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.repository.IProductRepository;

@Service
public class ProductService {
    
    @Autowired
    private IProductRepository productRepository;
    
    // Create a new product
    public Product createProduct(Product product) {
        if (product.getIsFavorite() == null) {
            product.setIsFavorite(false);
        }
        if (product.getCurrency() == null) {
            product.setCurrency("€");
        }
        return productRepository.save(product);
    }
    
    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // Get products by category
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategoryOrderByNameAsc(category);
    }
    
    // Get favorite products
    public List<Product> getFavoriteProducts() {
        return productRepository.findByIsFavoriteTrueOrderByNameAsc();
    }
    
    // Get products by brand
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrandOrderByNameAsc(brand);
    }
    
    // Get expired products
    public List<Product> getExpiredProducts() {
        return productRepository.findByExpiryDateBefore(LocalDate.now());
    }
    
    // Get products expiring soon (within 30 days)
    public List<Product> getProductsExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);
        return productRepository.findByExpiryDateBetween(today, in30Days);
    }
    
    // Get low stock products
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }
    
    // Search products by name
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }
    
    // Get products that should be used again
    public List<Product> getProductsToUseAgain() {
        return productRepository.findProductsToUseAgain();
    }
    
    // Update product
    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        
        if (updatedProduct.getName() != null) product.setName(updatedProduct.getName());
        if (updatedProduct.getCategory() != null) product.setCategory(updatedProduct.getCategory());
        if (updatedProduct.getBrand() != null) product.setBrand(updatedProduct.getBrand());
        if (updatedProduct.getQuantity() != null) product.setQuantity(updatedProduct.getQuantity());
        if (updatedProduct.getUnit() != null) product.setUnit(updatedProduct.getUnit());
        if (updatedProduct.getCost() != null) product.setCost(updatedProduct.getCost());
        if (updatedProduct.getCurrency() != null) product.setCurrency(updatedProduct.getCurrency());
        if (updatedProduct.getPurchaseDate() != null) product.setPurchaseDate(updatedProduct.getPurchaseDate());
        if (updatedProduct.getExpiryDate() != null) product.setExpiryDate(updatedProduct.getExpiryDate());
        if (updatedProduct.getNotes() != null) product.setNotes(updatedProduct.getNotes());
        if (updatedProduct.getImageUrl() != null) product.setImageUrl(updatedProduct.getImageUrl());
        if (updatedProduct.getIsFavorite() != null) product.setIsFavorite(updatedProduct.getIsFavorite());
        if (updatedProduct.getUsageFrequency() != null) product.setUsageFrequency(updatedProduct.getUsageFrequency());
        if (updatedProduct.getLastUsed() != null) product.setLastUsed(updatedProduct.getLastUsed());
        
        return productRepository.save(product);
    }
    
    // Mark product as used
    public Product markAsUsed(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        
        product.setLastUsed(LocalDate.now());
        return productRepository.save(product);
    }
    
    // Toggle favorite status
    public Product toggleFavorite(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        
        product.setIsFavorite(!product.getIsFavorite());
        return productRepository.save(product);
    }
    
    // Update quantity (add or subtract)
    public Product updateQuantity(Long id, Double quantityChange) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        
        Double currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0.0;
        product.setQuantity(currentQuantity + quantityChange);
        
        return productRepository.save(product);
    }
    
    // Delete product
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
