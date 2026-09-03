package com.example.productcrud.controller;

import com.example.productcrud.dto.ProductDTO;
import com.example.productcrud.exception.ResourceNotFoundException;
import com.example.productcrud.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController web layer tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("POST /api/v1/products should create a product and return 201")
    void createProduct_shouldReturn201() throws Exception {
        ProductDTO request = ProductDTO.builder().name("Wireless Mouse").productsku("WM-BLK-001").build();
        ProductDTO response = ProductDTO.builder().id(1L).name("Wireless Mouse").productsku("WM-BLK-001").build();

        when(productService.createProduct(any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.productsku").value("WM-BLK-001"));

        verify(productService, times(1)).createProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/products with blank name should return 400")
    void createProduct_withInvalidPayload_shouldReturn400() throws Exception {
        ProductDTO invalidRequest = ProductDTO.builder().name("").productsku("WM-BLK-001").build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} should return product when found")
    void getProductById_whenFound_shouldReturn200() throws Exception {
        ProductDTO response = ProductDTO.builder().id(1L).name("Wireless Mouse").productsku("WM-BLK-001").build();
        when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Wireless Mouse"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} should return 404 when not found")
    void getProductById_whenNotFound_shouldReturn404() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(get("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }

    @Test
    @DisplayName("GET /api/v1/products should return list of products")
    void getAllProducts_shouldReturn200() throws Exception {
        List<ProductDTO> products = Arrays.asList(
                ProductDTO.builder().id(1L).name("Wireless Mouse").productsku("WM-BLK-001").build(),
                ProductDTO.builder().id(2L).name("Mechanical Keyboard").productsku("MK-RGB-002").build()
        );
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Wireless Mouse"))
                .andExpect(jsonPath("$[1].name").value("Mechanical Keyboard"));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} should update and return 200")
    void updateProduct_whenFound_shouldReturn200() throws Exception {
        ProductDTO request = ProductDTO.builder().name("Wireless Mouse (2.4GHz)").productsku("WM-BLK-002").build();
        ProductDTO response = ProductDTO.builder().id(1L).name("Wireless Mouse (2.4GHz)").productsku("WM-BLK-002").build();

        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wireless Mouse (2.4GHz)"))
                .andExpect(jsonPath("$.productsku").value("WM-BLK-002"));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} should return 404 when product not found")
    void updateProduct_whenNotFound_shouldReturn404() throws Exception {
        ProductDTO request = ProductDTO.builder().name("Wireless Mouse (2.4GHz)").productsku("WM-BLK-002").build();

        when(productService.updateProduct(eq(99L), any(ProductDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(put("/api/v1/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} should return 204 when deleted")
    void deleteProduct_whenFound_shouldReturn204() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} should return 404 when not found")
    void deleteProduct_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/v1/products/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // static import helper for eq(); kept local to avoid pulling in unrelated matchers
    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
