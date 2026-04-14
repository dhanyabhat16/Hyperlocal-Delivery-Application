package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Get all products
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Products retrieved successfully")
                .data(products)
                .success(true)
                .build());
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> getProductById(@PathVariable Integer productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponseDTO.<ProductDTO>builder()
                .statusCode(200)
                .message("Product retrieved successfully")
                .data(productDTO)
                .success(true)
                .build());
    }
    
    /**
     * Search products by category
     */
    @GetMapping("/search/category")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> searchByCategory(@RequestParam String category) {
        List<ProductDTO> products = productService.searchByCategory(category);
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Products retrieved by category successfully")
                .data(products)
                .success(true)
                .build());
    }
    
    /**
     * Search products by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> searchByName(@RequestParam String name) {
        List<ProductDTO> products = productService.searchByName(name);
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Products retrieved by name successfully")
                .data(products)
                .success(true)
                .build());
    }
}
