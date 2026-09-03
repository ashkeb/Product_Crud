package com.example.productcrud;

import com.example.productcrud.dto.ProductDTO;
import com.example.productcrud.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end style test that boots the full Spring context (with an H2
 * in-memory DB via the "test" profile) and exercises the real CRUD flow
 * through the HTTP layer.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductCrudApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Spring application context loads successfully")
    void contextLoads() {
    }

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Full CRUD lifecycle works end-to-end through the REST API")
    void fullCrudLifecycle_shouldWorkEndToEnd() throws Exception {
        ProductDTO createRequest = ProductDTO.builder()
                .name("Wireless Mouse")
                .productsku("WM-BLK-001")
                .build();

        // CREATE
        String responseBody = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andReturn().getResponse().getContentAsString();

        ProductDTO created = objectMapper.readValue(responseBody, ProductDTO.class);
        Long id = created.getId();

        // RETRIEVE (by id)
        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productsku").value("WM-BLK-001"));

        // RETRIEVE (all)
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // UPDATE
        ProductDTO updateRequest = ProductDTO.builder()
                .name("Wireless Mouse (2.4GHz)")
                .productsku("WM-BLK-002")
                .build();

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wireless Mouse (2.4GHz)"))
                .andExpect(jsonPath("$.productsku").value("WM-BLK-002"));

        // DELETE
        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());
    }
}
