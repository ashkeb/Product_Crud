package com.example.productcrud.service;

import com.example.productcrud.dto.ProductDTO;
import com.example.productcrud.entity.Product;
import com.example.productcrud.exception.ResourceNotFoundException;
import com.example.productcrud.repository.ProductRepository;
import com.example.productcrud.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl unit tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Wireless Mouse")
                .productsku("WM-BLK-001")
                .build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Wireless Mouse")
                .productsku("WM-BLK-001")
                .build();
    }

    @Test
    @DisplayName("createProduct() should save and return the created product")
    void createProduct_shouldReturnSavedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.createProduct(productDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Wireless Mouse");
        assertThat(result.getProductsku()).isEqualTo("WM-BLK-001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("getProductById() should return product when found")
    void getProductById_whenFound_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getProductById() should throw ResourceNotFoundException when not found")
    void getProductById_whenNotFound_shouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("getAllProducts() should return list of all products")
    void getAllProducts_shouldReturnAllProducts() {
        Product product2 = Product.builder().id(2L).name("Mechanical Keyboard").productsku("MK-RGB-002").build();
        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));

        List<ProductDTO> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Wireless Mouse");
        assertThat(result.get(1).getName()).isEqualTo("Mechanical Keyboard");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("updateProduct() should update and return updated product when found")
    void updateProduct_whenFound_shouldReturnUpdatedProduct() {
        ProductDTO updateRequest = ProductDTO.builder()
                .name("Wireless Mouse (2.4GHz)")
                .productsku("WM-BLK-002")
                .build();

        Product updatedEntity = Product.builder()
                .id(1L)
                .name("Wireless Mouse (2.4GHz)")
                .productsku("WM-BLK-002")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedEntity);

        ProductDTO result = productService.updateProduct(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Wireless Mouse (2.4GHz)");
        assertThat(result.getProductsku()).isEqualTo("WM-BLK-002");
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct() should throw ResourceNotFoundException when product not found")
    void updateProduct_whenNotFound_shouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, productDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("deleteProduct() should delete when product exists")
    void deleteProduct_whenExists_shouldDelete() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct() should throw ResourceNotFoundException when product does not exist")
    void deleteProduct_whenNotExists_shouldThrowException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).deleteById(anyLong());
    }
}
