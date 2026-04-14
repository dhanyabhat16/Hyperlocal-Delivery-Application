package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.entity.Product;
import com.ecommerce.hyperlocaldelivery.service.ProductService;
import com.ecommerce.hyperlocaldelivery.service.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {


    @Autowired
    ProductService productService;
    
    /**
     * Get all products
     */
    @GetMapping("/public/products")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Products retrieved successfully")
                .data(products)
                .success(true)
                .build());
    }

    @PostMapping("/admin/{categoryId}/product")
    public ResponseEntity<ProductDTO>addProduct(@Valid @RequestBody ProductDTO productDTO,@PathVariable Long categoryId){
        ProductDTO resp=productService.addProduct(productDTO,categoryId);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/public/products/{productId}")
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
    @GetMapping("/public/{categoryId}/products")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> searchByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.searchByCategory(categoryId);
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
    @GetMapping("/public/products/search/{name}")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> searchByName(@PathVariable String name) {
        List<ProductDTO> products = productService.searchByName(name);
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Products retrieved by name successfully")
                .data(products)
                .success(true)
                .build());
    }

    @PutMapping("/admin/products/update/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer productId,@Valid @RequestBody ProductDTO productDTO){
        ProductDTO resp=productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/del/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Integer productId){
        ProductDTO resp=productService.deleteProduct(productId);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO>updateProductImage(@PathVariable Integer productId, @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO resp=productService.updateProductImage(productId,image);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }
}
