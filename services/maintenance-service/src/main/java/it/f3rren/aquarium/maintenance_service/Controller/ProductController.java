package it.f3rren.aquarium.maintenance_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import it.f3rren.aquarium.maintenance_service.dto.ApiResponseDTO;
import it.f3rren.aquarium.maintenance_service.dto.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.QuantityChangeDTO;
import it.f3rren.aquarium.maintenance_service.dto.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.service.IProductService;

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "API for managing aquarium products")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve products, optionally filtered by category, brand, or search term")
    public ResponseEntity<ApiResponseDTO<List<Product>>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean favorites,
            @RequestParam(required = false) Boolean expired,
            @RequestParam(required = false) Boolean expiringSoon,
            @RequestParam(required = false) Boolean lowStock,
            @RequestParam(required = false) Boolean shouldUseAgain) {

        List<Product> products;

        if (favorites != null && favorites) {
            products = productService.getFavoriteProducts();
        } else if (expired != null && expired) {
            products = productService.getExpiredProducts();
        } else if (expiringSoon != null && expiringSoon) {
            products = productService.getProductsExpiringSoon();
        } else if (lowStock != null && lowStock) {
            products = productService.getLowStockProducts();
        } else if (shouldUseAgain != null && shouldUseAgain) {
            products = productService.getProductsToUseAgain();
        } else if (category != null) {
            ProductCategory cat = ProductCategory.valueOf(category.toUpperCase());
            products = productService.getProductsByCategory(cat);
        } else if (brand != null) {
            products = productService.getProductsByBrand(brand);
        } else if (search != null) {
            products = productService.searchProductsByName(search);
        } else {
            products = productService.getAllProducts();
        }

        ApiResponseDTO<List<Product>> response = new ApiResponseDTO<>(
                true,
                "Products retrieved successfully",
                products,
                Map.of("count", products.size()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all product categories", description = "Retrieve the list of all available product categories")
    public ResponseEntity<ApiResponseDTO<ProductCategory[]>> getCategories() {
        ProductCategory[] categories = ProductCategory.values();

        ApiResponseDTO<ProductCategory[]> response = new ApiResponseDTO<>(
                true,
                "Categories retrieved successfully",
                categories,
                Map.of("count", categories.length));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    public ResponseEntity<ApiResponseDTO<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);

        ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                true,
                "Product retrieved successfully",
                product,
                null);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product")
    public ResponseEntity<ApiResponseDTO<Product>> createProduct(@Valid @RequestBody CreateProductDTO dto) {
        Product created = productService.createProduct(dto);

        ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                true,
                "Product created successfully",
                created,
                null);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product", description = "Update details of a specific product")
    public ResponseEntity<ApiResponseDTO<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDTO dto) {

        Product updated = productService.updateProduct(id, dto);

        ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                true,
                "Product updated successfully",
                updated,
                null);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/mark-used")
    @Operation(summary = "Mark product as used", description = "Update the last used date of a product to today")
    public ResponseEntity<ApiResponseDTO<Product>> markAsUsed(@PathVariable Long id) {
        Product updated = productService.markAsUsed(id);

        ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                true,
                "Product marked as used",
                updated,
                null);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-favorite")
    @Operation(summary = "Toggle favorite status", description = "Toggle the favorite status of a product")
    public ResponseEntity<ApiResponseDTO<Product>> toggleFavorite(@PathVariable Long id) {
        Product updated = productService.toggleFavorite(id);

        ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                true,
                "Favorite status toggled",
                updated,
                null);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/quantity")
    @Operation(summary = "Update product quantity", description = "Add or subtract from product quantity")
    public ResponseEntity<ApiResponseDTO<Product>> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody QuantityChangeDTO dto) {

        Product updated = productService.updateQuantity(id, dto.getChange());

        ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                true,
                "Quantity updated successfully",
                updated,
                null);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a specific product")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(
                true,
                "Product deleted successfully",
                null,
                null);

        return ResponseEntity.ok(response);
    }
}
