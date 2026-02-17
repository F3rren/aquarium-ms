package it.f3rren.aquarium.maintenance_service.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.maintenance_service.dto.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.repository.IProductRepository;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product createProduct(CreateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        product.setQuantity(dto.getQuantity());
        product.setUnit(dto.getUnit());
        product.setCost(dto.getCost());
        product.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "€");
        product.setPurchaseDate(dto.getPurchaseDate());
        product.setExpiryDate(dto.getExpiryDate());
        product.setNotes(dto.getNotes());
        product.setImageUrl(dto.getImageUrl());
        product.setIsFavorite(dto.getIsFavorite() != null ? dto.getIsFavorite() : false);
        product.setUsageFrequency(dto.getUsageFrequency());

        log.info("Creating product: {}", dto.getName());
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategoryOrderByNameAsc(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getFavoriteProducts() {
        return productRepository.findByIsFavoriteTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrandOrderByNameAsc(brand);
    }

    @Transactional(readOnly = true)
    public List<Product> getExpiredProducts() {
        return productRepository.findByExpiryDateBefore(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);
        return productRepository.findByExpiryDateBetween(today, in30Days);
    }

    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsToUseAgain() {
        return productRepository.findProductsToUseAgain();
    }

    @Transactional
    public Product updateProduct(Long id, UpdateProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getQuantity() != null) product.setQuantity(dto.getQuantity());
        if (dto.getUnit() != null) product.setUnit(dto.getUnit());
        if (dto.getCost() != null) product.setCost(dto.getCost());
        if (dto.getCurrency() != null) product.setCurrency(dto.getCurrency());
        if (dto.getPurchaseDate() != null) product.setPurchaseDate(dto.getPurchaseDate());
        if (dto.getExpiryDate() != null) product.setExpiryDate(dto.getExpiryDate());
        if (dto.getNotes() != null) product.setNotes(dto.getNotes());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
        if (dto.getIsFavorite() != null) product.setIsFavorite(dto.getIsFavorite());
        if (dto.getUsageFrequency() != null) product.setUsageFrequency(dto.getUsageFrequency());
        if (dto.getLastUsed() != null) product.setLastUsed(dto.getLastUsed());

        log.info("Updating product {}", id);
        return productRepository.save(product);
    }

    @Transactional
    public Product markAsUsed(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setLastUsed(LocalDate.now());
        log.info("Marking product {} as used", id);
        return productRepository.save(product);
    }

    @Transactional
    public Product toggleFavorite(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setIsFavorite(!product.getIsFavorite());
        log.info("Toggling favorite for product {} -> {}", id, product.getIsFavorite());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateQuantity(Long id, Double quantityChange) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        Double currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0.0;
        product.setQuantity(currentQuantity + quantityChange);

        log.info("Updating quantity for product {}: {} -> {}", id, currentQuantity, product.getQuantity());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        log.info("Deleting product {}", id);
        productRepository.deleteById(id);
    }
}
