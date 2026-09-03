package com.example.productcrud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product, used at the API boundary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product details payload")
public class ProductDTO {

    @Schema(description = "Auto-generated product id", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Schema(description = "Product name", example = "Wireless Mouse")
    private String name;

    @NotBlank(message = "Product SKU must not be blank")
    @Schema(description = "Unique Stock Keeping Unit (SKU) code for the product", example = "WM-BLK-001")
    private String productsku;
}
