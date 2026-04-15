package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.entity.Role;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.entity.Warehouse;
import com.ecommerce.hyperlocaldelivery.service.ProductService;
import com.ecommerce.hyperlocaldelivery.service.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;
    private final UserContextService userContextService;
    
    /**
     * Get all products (filtered by warehouse if authenticated)
     */
    @GetMapping("/public/products")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> getAllProducts() {
        Warehouse warehouse = userContextService.getCurrentWarehouse();
        List<ProductDTO> products;
        if (warehouse != null) {
            // Filter products by user's warehouse
            products = productService.getProductsByWarehouse(warehouse);
        } else {
            // Public: show all products
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Products retrieved successfully")
                .data(products)
                .success(true)
                .build());
    }

    @PostMapping("/warehouse/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductDTO productDTO,
            @RequestParam(required = false) Integer warehouseId) {
        User currentUser = userContextService.getCurrentUserOrThrow();

        Integer effectiveWarehouseId;
        if (currentUser.getRole() == Role.WAREHOUSE) {
            Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
            effectiveWarehouseId = warehouse.getWarehouseId();
        } else if (currentUser.getRole() == Role.ADMIN) {
            if (warehouseId == null) {
                return ResponseEntity.badRequest().build();
            }
            effectiveWarehouseId = warehouseId;
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ProductDTO resp = productService.addProduct(productDTO, categoryId, effectiveWarehouseId);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> getProductById(@PathVariable Integer productId) {
        Warehouse warehouse = userContextService.getCurrentWarehouse();
        ProductDTO productDTO;
        if (warehouse != null) {
            productDTO = productService.getProductByIdAndWarehouse(productId, warehouse.getWarehouseId());
        } else {
            productDTO = productService.getProductById(productId);
        }
        return ResponseEntity.ok(ApiResponseDTO.<ProductDTO>builder()
                .statusCode(200)
                .message("Product retrieved successfully")
                .data(productDTO)
                .success(true)
                .build());
    }
    
    /**
     * Search products by category (filtered by warehouse if authenticated)
     */
    @GetMapping("/public/{categoryId}/products")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> searchByCategory(@PathVariable Long categoryId) {
        Warehouse warehouse = userContextService.getCurrentWarehouse();
        List<ProductDTO> products;
        if (warehouse != null) {
            products = productService.searchByCategoryAndWarehouse(categoryId, warehouse.getWarehouseId());
        } else {
            products = productService.searchByCategory(categoryId);
        }
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
        Warehouse warehouse = userContextService.getCurrentWarehouse();
        List<ProductDTO> products;
        if (warehouse != null) {
            products = productService.searchByNameAndWarehouse(name, warehouse.getWarehouseId());
        } else {
            products = productService.searchByName(name);
        }
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Products retrieved by name successfully")
                .data(products)
                .success(true)
                .build());
    }

    @PutMapping("/admin/products/update/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer productId,@Valid @RequestBody ProductDTO productDTO){
        ProductDTO resp=productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/del/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Integer productId){
        ProductDTO resp=productService.deleteProduct(productId);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO>updateProductImage(@PathVariable Integer productId, @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO resp=productService.updateProductImage(productId,image);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }
}
