package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.*;
import com.ecommerce.hyperlocaldelivery.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {
    
    private final CartService cartService;
    
    /**
     * Get or create cart for customer
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponseDTO<CartDTO>> getOrCreateCart(@PathVariable Integer customerId) {
        CartDTO cartDTO = cartService.getOrCreateCart(customerId);
        return ResponseEntity.ok(ApiResponseDTO.<CartDTO>builder()
                .statusCode(200)
                .message("Cart retrieved successfully")
                .data(cartDTO)
                .success(true)
                .build());
    }
    
    /**
     * Add product to cart
     */
    @PostMapping("/{customerId}/items")
    public ResponseEntity<ApiResponseDTO<CartDTO>> addToCart(
            @PathVariable Integer customerId,
            @RequestBody AddToCartDTO addToCartDTO) {
        CartDTO cartDTO = cartService.addToCart(customerId, addToCartDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<CartDTO>builder()
                        .statusCode(201)
                        .message("Product added to cart successfully")
                        .data(cartDTO)
                        .success(true)
                        .build());
    }
    
    /**
     * Update cart item quantity
     */
    @PutMapping("/{customerId}/items")
    public ResponseEntity<ApiResponseDTO<CartDTO>> updateCartItemQuantity(
            @PathVariable Integer customerId,
            @RequestBody UpdateQuantityDTO updateDTO) {
        CartDTO cartDTO = cartService.updateCartItemQuantity(customerId, updateDTO);
        return ResponseEntity.ok(ApiResponseDTO.<CartDTO>builder()
                .statusCode(200)
                .message("Cart item quantity updated successfully")
                .data(cartDTO)
                .success(true)
                .build());
    }
    
    /**
     * Remove item from cart
     */
    @DeleteMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseDTO<CartDTO>> removeFromCart(
            @PathVariable Integer customerId,
            @PathVariable Integer cartItemId) {
        CartDTO cartDTO = cartService.removeFromCart(customerId, cartItemId);
        return ResponseEntity.ok(ApiResponseDTO.<CartDTO>builder()
                .statusCode(200)
                .message("Item removed from cart successfully")
                .data(cartDTO)
                .success(true)
                .build());
    }
    
    /**
     * Clear entire cart
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponseDTO<Void>> clearCart(@PathVariable Integer customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok(ApiResponseDTO.<Void>builder()
                .statusCode(200)
                .message("Cart cleared successfully")
                .success(true)
                .build());
    }
}
