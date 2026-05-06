package it.f3rren.aquarium.maintenance_service.exception;

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
import org.springframework.test.web.servlet.MockMvc;

import it.f3rren.aquarium.maintenance_service.controller.ProductController;
import it.f3rren.aquarium.maintenance_service.service.IProductService;

/**
 * Covers exception-handler branches not exercised by ProductControllerTest.
 */
@WebMvcTest(ProductController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductService productService;

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
}
