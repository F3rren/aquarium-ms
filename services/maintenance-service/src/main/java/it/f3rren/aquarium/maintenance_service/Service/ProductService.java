package it.f3rren.aquarium.maintenance_service.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.maintenance_service.dto.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.ProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.ProductFilter;
import it.f3rren.aquarium.maintenance_service.dto.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.repository.IProductRepository;

@Service
public class ProductService implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductDTO createProduct(CreateProductDTO dto) {
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
        return toDTO(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProducts(ProductFilter filter) {
        if (Boolean.TRUE.equals(filter.getFavorites())) {
            return toDTOList(productRepository.findByIsFavoriteTrueOrderByNameAsc());
        } else if (Boolean.TRUE.equals(filter.getExpired())) {
            return toDTOList(productRepository.findByExpiryDateBefore(LocalDate.now()));
        } else if (Boolean.TRUE.equals(filter.getExpiringSoon())) {
            LocalDate today = LocalDate.now();
            return toDTOList(productRepository.findByExpiryDateBetween(today, today.plusDays(30)));
        } else if (Boolean.TRUE.equals(filter.getLowStock())) {
            return toDTOList(productRepository.findLowStockProducts());
        } else if (Boolean.TRUE.equals(filter.getShouldUseAgain())) {
            return toDTOList(productRepository.findProductsToUseAgain());
        } else if (filter.getCategory() != null) {
            ProductCategory cat = ProductCategory.valueOf(filter.getCategory().toUpperCase());
            return toDTOList(productRepository.findByCategoryOrderByNameAsc(cat));
        } else if (filter.getBrand() != null) {
            return toDTOList(productRepository.findByBrandOrderByNameAsc(filter.getBrand()));
        } else if (filter.getSearch() != null) {
            return toDTOList(productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(filter.getSearch()));
        }
        return toDTOList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        return toDTO(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id)));
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(ProductCategory category) {
        return toDTOList(productRepository.findByCategoryOrderByNameAsc(category));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductDTO dto) {
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
        return toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO markAsUsed(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setLastUsed(LocalDate.now());
        log.info("Marking product {} as used", id);
        return toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO toggleFavorite(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setIsFavorite(!product.getIsFavorite());
        log.info("Toggling favorite for product {} -> {}", id, product.getIsFavorite());
        return toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateQuantity(Long id, Double quantityChange) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        double currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0.0;
        product.setQuantity(currentQuantity + quantityChange);

        log.info("Updating quantity for product {}: {} -> {}", id, currentQuantity, product.getQuantity());
        return toDTO(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        log.info("Deleting product {}", id);
        productRepository.deleteById(id);
    }

    private ProductDTO toDTO(Product product) {
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

    private List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream().map(this::toDTO).toList();
    }
}
