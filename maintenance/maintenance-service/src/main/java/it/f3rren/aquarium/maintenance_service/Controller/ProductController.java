package it.f3rren.aquarium.maintenance_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.f3rren.aquarium.maintenance_service.model.Product;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.service.ProductService;

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "API for managing aquarium products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // GET - Get all products
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products, optionally filtered by category, brand, or search term")
    public ResponseEntity<?> getAllProducts(
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
            try {
                ProductCategory cat = ProductCategory.valueOf(category.toUpperCase());
                products = productService.getProductsByCategory(cat);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid category: " + category
                ));
            }
        } else if (brand != null) {
            products = productService.getProductsByBrand(brand);
        } else if (search != null) {
            products = productService.searchProductsByName(search);
        } else {
            products = productService.getAllProducts();
        }
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Products retrieved successfully",
            "data", products,
            "metadata", Map.of(
                "count", products.size()
            )
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // GET - Get product by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(product -> {
                Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Product retrieved successfully",
                    "data", product
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            })
            .orElseGet(() -> {
                Map<String, Object> response = Map.of(
                    "success", false,
                    "message", "Product not found with ID: " + id
                );
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            });
    }
    
    // POST - Create a new product
    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product")
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Product created successfully",
            "data", created
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // PUT - Update an existing product
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product", description = "Update details of a specific product")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        
        Product updated = productService.updateProduct(id, product);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Product updated successfully",
            "data", updated
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // PATCH - Mark product as used
    @PatchMapping("/{id}/mark-used")
    @Operation(summary = "Mark product as used", description = "Update the last used date of a product to today")
    public ResponseEntity<?> markAsUsed(@PathVariable Long id) {
        Product updated = productService.markAsUsed(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Product marked as used",
            "data", updated
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // PATCH - Toggle favorite status
    @PatchMapping("/{id}/toggle-favorite")
    @Operation(summary = "Toggle favorite status", description = "Toggle the favorite status of a product")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id) {
        Product updated = productService.toggleFavorite(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Favorite status toggled",
            "data", updated
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // PATCH - Update quantity
    @PatchMapping("/{id}/quantity")
    @Operation(summary = "Update product quantity", description = "Add or subtract from product quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Double> requestBody) {
        
        Double quantityChange = requestBody.get("change");
        if (quantityChange == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Missing 'change' field in request body"
            ));
        }
        
        Product updated = productService.updateQuantity(id, quantityChange);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Quantity updated successfully",
            "data", updated
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // DELETE - Delete a product
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a specific product")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        
        Map<String, Object> response = Map.of(
            "success", true,
            "message", "Product deleted successfully"
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
