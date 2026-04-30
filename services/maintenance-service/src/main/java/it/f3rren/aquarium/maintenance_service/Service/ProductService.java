package it.f3rren.aquarium.maintenance_service.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.f3rren.aquarium.maintenance_service.dto.request.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.ProductFilter;
import it.f3rren.aquarium.maintenance_service.dto.request.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.response.ProductDTO;
import it.f3rren.aquarium.maintenance_service.mapper.ProductMapper;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.repository.IProductRepository;

@Service
public class ProductService implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final IProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(IProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
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
        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProducts(ProductFilter filter) {
        if (Boolean.TRUE.equals(filter.getFavorites())) {
            return productMapper.toDTOList(productRepository.findByIsFavoriteTrueOrderByNameAsc());
        } else if (Boolean.TRUE.equals(filter.getExpired())) {
            return productMapper.toDTOList(productRepository.findByExpiryDateBefore(LocalDate.now()));
        } else if (Boolean.TRUE.equals(filter.getExpiringSoon())) {
            LocalDate today = LocalDate.now();
            return productMapper.toDTOList(productRepository.findByExpiryDateBetween(today, today.plusDays(30)));
        } else if (Boolean.TRUE.equals(filter.getLowStock())) {
            return productMapper.toDTOList(productRepository.findLowStockProducts());
        } else if (Boolean.TRUE.equals(filter.getShouldUseAgain())) {
            return productMapper.toDTOList(productRepository.findProductsToUseAgain());
        } else if (filter.getCategory() != null) {
            ProductCategory cat = ProductCategory.valueOf(filter.getCategory().toUpperCase());
            return productMapper.toDTOList(productRepository.findByCategoryOrderByNameAsc(cat));
        } else if (filter.getBrand() != null) {
            return productMapper.toDTOList(productRepository.findByBrandOrderByNameAsc(filter.getBrand()));
        } else if (filter.getSearch() != null) {
            return productMapper.toDTOList(productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(filter.getSearch()));
        }
        return productMapper.toDTOList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        return productMapper.toDTO(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id)));
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(ProductCategory category) {
        return productMapper.toDTOList(productRepository.findByCategoryOrderByNameAsc(category));
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
        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO markAsUsed(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setLastUsed(LocalDate.now());
        log.info("Marking product {} as used", id);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO toggleFavorite(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setIsFavorite(!product.getIsFavorite());
        log.info("Toggling favorite for product {} -> {}", id, product.getIsFavorite());
        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateQuantity(Long id, Double quantityChange) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        double currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0.0;
        product.setQuantity(currentQuantity + quantityChange);

        log.info("Updating quantity for product {}: {} -> {}", id, currentQuantity, product.getQuantity());
        return productMapper.toDTO(productRepository.save(product));
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
