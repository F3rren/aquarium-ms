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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import it.f3rren.aquarium.maintenance_service.dto.request.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.ProductFilter;
import it.f3rren.aquarium.maintenance_service.dto.request.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.response.ProductDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.mapper.ProductMapper;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.repository.IProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Spy
    private ProductMapper productMapper = new ProductMapper();

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

        @Test
        void savesWithExplicitCurrencyAndFavorite() {
            CreateProductDTO dto = new CreateProductDTO();
            dto.setName("Reef Salt");
            dto.setCategory(ProductCategory.WATER_TREATMENT);
            dto.setCurrency("$");
            dto.setIsFavorite(true);

            when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

            productService.createProduct(dto);

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

        @Test
        void returnsExpiredProducts() {
            when(productRepository.findByExpiryDateBefore(any())).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter(null, null, null, null, true, null, null, null));

            assertEquals(1, result.size());
            verify(productRepository).findByExpiryDateBefore(any());
        }

        @Test
        void returnsExpiringSoonProducts() {
            when(productRepository.findByExpiryDateBetween(any(), any())).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter(null, null, null, null, null, true, null, null));

            assertEquals(1, result.size());
            verify(productRepository).findByExpiryDateBetween(any(), any());
        }

        @Test
        void returnsLowStockProducts() {
            when(productRepository.findLowStockProducts()).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter(null, null, null, null, null, null, true, null));

            assertEquals(1, result.size());
            verify(productRepository).findLowStockProducts();
        }

        @Test
        void returnsShouldUseAgainProducts() {
            when(productRepository.findProductsToUseAgain()).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter(null, null, null, null, null, null, null, true));

            assertEquals(1, result.size());
            verify(productRepository).findProductsToUseAgain();
        }

        @Test
        void returnsByCategory() {
            when(productRepository.findByCategoryOrderByNameAsc(ProductCategory.FOOD)).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter("FOOD", null, null, null, null, null, null, null));

            assertEquals(1, result.size());
            verify(productRepository).findByCategoryOrderByNameAsc(ProductCategory.FOOD);
        }

        @Test
        void returnsByBrand() {
            when(productRepository.findByBrandOrderByNameAsc("Seachem")).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter(null, "Seachem", null, null, null, null, null, null));

            assertEquals(1, result.size());
            verify(productRepository).findByBrandOrderByNameAsc("Seachem");
        }

        @Test
        void returnsBySearch() {
            when(productRepository.findByNameContainingIgnoreCaseOrderByNameAsc("Prime")).thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProducts(new ProductFilter(null, null, "Prime", null, null, null, null, null));

            assertEquals(1, result.size());
            verify(productRepository).findByNameContainingIgnoreCaseOrderByNameAsc("Prime");
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

        @Test
        void treatsNullCurrentQuantityAsZero() {
            sampleProduct.setQuantity(null);
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

            ProductDTO result = productService.updateQuantity(1L, 50.0);

            assertEquals(50.0, result.getQuantity());
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

    @Nested
    class UpdateProduct {

        @Test
        void updatesOnlyNonNullFields() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            UpdateProductDTO dto = new UpdateProductDTO();
            dto.setName("Updated Name");

            ProductDTO result = productService.updateProduct(1L, dto);

            assertEquals("Updated Name", result.getName());
            verify(productRepository).save(sampleProduct);
        }

        @Test
        void updatesAllOptionalFields() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

            UpdateProductDTO dto = new UpdateProductDTO();
            dto.setName("Full Update");
            dto.setCategory(ProductCategory.FOOD);
            dto.setBrand("Aquatic");
            dto.setQuantity(200.0);
            dto.setUnit("ml");
            dto.setCost(15.99);
            dto.setCurrency("$");
            dto.setNotes("Updated notes");
            dto.setImageUrl("https://example.com/img.jpg");
            dto.setIsFavorite(true);
            dto.setUsageFrequency(14);

            ProductDTO result = productService.updateProduct(1L, dto);

            assertEquals("Full Update", result.getName());
            assertEquals(ProductCategory.FOOD, result.getCategory());
            assertEquals("Aquatic", result.getBrand());
            assertEquals(200.0, result.getQuantity());
        }

        @Test
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.updateProduct(99L, new UpdateProductDTO()));
        }
    }

    @Nested
    class DeleteProduct {

        @Test
        void deletesExistingProduct() {
            when(productRepository.existsById(1L)).thenReturn(true);

            productService.deleteProduct(1L);

            verify(productRepository).deleteById(1L);
        }

        @Test
        void throwsWhenNotFound() {
            when(productRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> productService.deleteProduct(99L));

            verify(productRepository, never()).deleteById(any());
        }
    }

    @Nested
    class GetProductsByCategory {

        @Test
        void returnsDtoListForCategory() {
            when(productRepository.findByCategoryOrderByNameAsc(ProductCategory.FOOD))
                    .thenReturn(List.of(sampleProduct));

            List<ProductDTO> result = productService.getProductsByCategory(ProductCategory.FOOD);

            assertEquals(1, result.size());
            assertEquals("Seachem Prime", result.get(0).getName());
            verify(productRepository).findByCategoryOrderByNameAsc(ProductCategory.FOOD);
        }
    }
}
