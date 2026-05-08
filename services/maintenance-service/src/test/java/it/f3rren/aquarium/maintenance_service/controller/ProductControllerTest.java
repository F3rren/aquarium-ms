package it.f3rren.aquarium.maintenance_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.maintenance_service.dto.request.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.QuantityChangeDTO;
import it.f3rren.aquarium.maintenance_service.dto.request.UpdateProductDTO;
import it.f3rren.aquarium.maintenance_service.dto.response.ProductDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.service.IProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProductService productService;

    private ProductDTO sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new ProductDTO();
        sampleProduct.setId(1L);
        sampleProduct.setName("Seachem Prime");
        sampleProduct.setCategory(ProductCategory.WATER_TREATMENT);
    }

    @Nested
    class GetAllProducts {

        @Test
        void returnsAllProducts() throws Exception {
            when(productService.getProducts(any())).thenReturn(List.of(sampleProduct));

            mockMvc.perform(get("/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].name").value("Seachem Prime"));
        }

        @Test
        void returnsFilteredProducts() throws Exception {
            when(productService.getProducts(any())).thenReturn(List.of(sampleProduct));

            mockMvc.perform(get("/products")
                            .param("category", "WATER_TREATMENT")
                            .param("favorites", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    class GetCategories {

        @Test
        void returnsAllCategories() throws Exception {
            mockMvc.perform(get("/products/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    class GetProductById {

        @Test
        void returnsProductWhenFound() throws Exception {
            when(productService.getProductById(1L)).thenReturn(sampleProduct);

            mockMvc.perform(get("/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Seachem Prime"));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            when(productService.getProductById(99L))
                    .thenThrow(new ResourceNotFoundException("Product not found with ID: 99"));

            mockMvc.perform(get("/products/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class CreateProduct {

        @Test
        void createsSuccessfully() throws Exception {
            CreateProductDTO dto = new CreateProductDTO();
            dto.setName("Seachem Prime");
            dto.setCategory(ProductCategory.WATER_TREATMENT);

            when(productService.createProduct(any(CreateProductDTO.class))).thenReturn(sampleProduct);

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Seachem Prime"));
        }

        @Test
        void returns400WhenNameIsBlank() throws Exception {
            CreateProductDTO dto = new CreateProductDTO();
            dto.setCategory(ProductCategory.FOOD);
            // name is null → @NotBlank fails

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class UpdateProduct {

        @Test
        void updatesSuccessfully() throws Exception {
            UpdateProductDTO dto = new UpdateProductDTO();
            dto.setName("Updated Name");

            when(productService.updateProduct(eq(1L), any(UpdateProductDTO.class))).thenReturn(sampleProduct);

            mockMvc.perform(put("/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            UpdateProductDTO dto = new UpdateProductDTO();
            dto.setName("Updated");

            when(productService.updateProduct(eq(99L), any()))
                    .thenThrow(new ResourceNotFoundException("Product not found with ID: 99"));

            mockMvc.perform(put("/products/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class MarkAsUsed {

        @Test
        void marksSuccessfully() throws Exception {
            when(productService.markAsUsed(1L)).thenReturn(sampleProduct);

            mockMvc.perform(patch("/products/1/mark-used"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    class ToggleFavorite {

        @Test
        void togglesSuccessfully() throws Exception {
            when(productService.toggleFavorite(1L)).thenReturn(sampleProduct);

            mockMvc.perform(patch("/products/1/toggle-favorite"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    class UpdateQuantity {

        @Test
        void updatesQuantitySuccessfully() throws Exception {
            QuantityChangeDTO dto = new QuantityChangeDTO();
            dto.setChange(10.0);

            when(productService.updateQuantity(eq(1L), eq(10.0))).thenReturn(sampleProduct);

            mockMvc.perform(patch("/products/1/quantity")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void returns400WhenChangeIsNull() throws Exception {
            QuantityChangeDTO dto = new QuantityChangeDTO();
            // change is null → @NotNull fails

            mockMvc.perform(patch("/products/1/quantity")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    class DeleteProduct {

        @Test
        void deletesSuccessfully() throws Exception {
            doNothing().when(productService).deleteProduct(1L);

            mockMvc.perform(delete("/products/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Product not found with ID: 99"))
                    .when(productService).deleteProduct(99L);

            mockMvc.perform(delete("/products/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
