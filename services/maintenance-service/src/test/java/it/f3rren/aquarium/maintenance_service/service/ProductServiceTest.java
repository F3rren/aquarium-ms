package it.f3rren.aquarium.maintenance_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.maintenance_service.dto.request.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.ProductFilter;
import it.f3rren.aquarium.maintenance_service.dto.response.ProductDTO;
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

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Seachem Prime");
        sampleProduct.setCategory(ProductCategory.WATER_TREATMENT);
        sampleProduct.setIsFavorite(false);
        sampleProduct.setQuantity(100.0);
    }

    @Nested
    class CreateProduct {

        @Test
        void savesAndReturnsDto() {
            CreateProductDTO dto = new CreateProductDTO();
            dto.setName("Seachem Prime");
            dto.setCategory(ProductCategory.WATER_TREATMENT);

            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            ProductDTO result = productService.createProduct(dto);

            assertEquals("Seachem Prime", result.getName());
            verify(productRepository).save(any(Product.class));
        }
    }

    @Nested
    class GetProducts {

        @Test
        void returnsAllProductsWhenNoFilter() {
            when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter());

            assertEquals(1, result.size());
            verify(productRepository).findAll();
        }

        @Test
        void returnsFavoritesWhenFlagSet() {
            when(productRepository.findByIsFavoriteTrueOrderByNameAsc()).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter(null, null, null, true, null, null, null, null));

            assertEquals(1, result.size());
            verify(productRepository).findByIsFavoriteTrueOrderByNameAsc();
        }
    }

    @Nested
    class GetProductById {

        @Test
        void returnsDto() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            ProductDTO result = productService.getProductById(1L);

            assertEquals("Seachem Prime", result.getName());
        }

        @Test
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        }
    }

    @Nested
    class ToggleFavorite {

        @Test
        void switchesFavoriteStatus() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            ProductDTO result = productService.toggleFavorite(1L);

            assertTrue(result.getIsFavorite());
            verify(productRepository).save(sampleProduct);
        }
    }

    @Nested
    class UpdateQuantity {

        @Test
        void addsToCurrentQuantity() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            ProductDTO result = productService.updateQuantity(1L, -25.0);

            assertEquals(75.0, result.getQuantity());
        }
    }

    @Nested
    class MarkAsUsed {

        @Test
        void setsLastUsedToToday() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            ProductDTO result = productService.markAsUsed(1L);

            assertNotNull(result.getLastUsed());
        }
    }
}
