package it.f3rren.aquarium.maintenance_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.maintenance_service.dto.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.repository.IProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_savesAndReturnsProduct() {
        CreateProductDTO dto = new CreateProductDTO();
        dto.setName("Seachem Prime");
        dto.setCategory(ProductCategory.WATER_TREATMENT);

        Product saved = new Product();
        saved.setId(1L);
        saved.setName("Seachem Prime");
        saved.setCategory(ProductCategory.WATER_TREATMENT);

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.createProduct(dto);

        assertEquals("Seachem Prime", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getAllProducts_returnsList() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Seachem Prime");

        when(productRepository.findAll()).thenReturn(List.of(p1));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_returnsProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Seachem Prime");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertEquals("Seachem Prime", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_throwsWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void toggleFavorite_switchesFavoriteStatus() {
        Product product = new Product();
        product.setId(1L);
        product.setIsFavorite(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.toggleFavorite(1L);

        assertTrue(result.getIsFavorite());
        verify(productRepository).save(product);
    }

    @Test
    void updateQuantity_addsToCurrentQuantity() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(100.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateQuantity(1L, -25.0);

        assertEquals(75.0, result.getQuantity());
        verify(productRepository).save(product);
    }
}
