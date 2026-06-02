package it.f3rren.aquarium.maintenance_service.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.f3rren.aquarium.maintenance_service.controller.MaintenanceTaskController;
import it.f3rren.aquarium.maintenance_service.controller.ProductController;
import it.f3rren.aquarium.maintenance_service.dto.request.CreateProductDTO;
import it.f3rren.aquarium.maintenance_service.exception.ResourceNotFoundException;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;
import it.f3rren.aquarium.maintenance_service.service.IMaintenanceTaskService;
import it.f3rren.aquarium.maintenance_service.service.IProductService;

/**
 * Covers exception-handler branches not exercised by ProductControllerTest or MaintenanceTaskControllerTest.
 */
@WebMvcTest({ProductController.class, MaintenanceTaskController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProductService productService;

    @MockBean
    private IMaintenanceTaskService taskService;

    @Nested
    @DisplayName("Generic Exception → 500")
    class GenericExceptionTests {

        @Test
        @DisplayName("should return 500 for unexpected RuntimeException from service")
        void shouldReturn500ForUnexpectedException() throws Exception {
            when(productService.getProductById(anyLong()))
                    .thenThrow(new RuntimeException("unexpected error"));

            mockMvc.perform(get("/products/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("An internal error occurred"));
        }
    }

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException → 400")
    class TypeMismatchTests {

        @Test
        @DisplayName("should return 400 for non-numeric product ID in path")
        void shouldReturn400ForInvalidIdType() throws Exception {
            mockMvc.perform(get("/products/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("ResourceNotFoundException → 404")
    class ResourceNotFoundTests {

        @Test
        @DisplayName("should return 404 with error message")
        void shouldReturn404() throws Exception {
            when(productService.getProductById(99L))
                    .thenThrow(new ResourceNotFoundException("Product not found with ID: 99"));

            mockMvc.perform(get("/products/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Product not found with ID: 99"));
        }
    }

    @Nested
    @DisplayName("HttpMessageNotReadableException → 400")
    class HttpMessageNotReadableTests {

        @Test
        @DisplayName("should return 400 for unrecognized enum value in request body")
        void shouldReturn400ForInvalidEnumValue() throws Exception {
            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Test\",\"category\":\"INVALID_ENUM\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Malformed or unreadable request body"));
        }
    }

    @Nested
    @DisplayName("DataIntegrityViolationException → 409")
    class DataIntegrityTests {

        @Test
        @DisplayName("should return 409 Conflict on DB constraint violation")
        void shouldReturn409OnConstraintViolation() throws Exception {
            CreateProductDTO dto = new CreateProductDTO();
            dto.setName("Test Product");
            dto.setCategory(ProductCategory.FOOD);

            when(productService.createProduct(any(CreateProductDTO.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Data integrity constraint violated"));
        }
    }
}
