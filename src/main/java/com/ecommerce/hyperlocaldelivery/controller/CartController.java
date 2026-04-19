package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.AddToCartDTO;
import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.CartDTO;
import com.ecommerce.hyperlocaldelivery.dto.UpdateQuantityDTO;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.service.ICartService;
import com.ecommerce.hyperlocaldelivery.service.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {
    private final ICartService cartService;
    private final UserContextService userContextService;
    
    /**
     * Get user's cart
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<CartDTO>> getCart() {
        User user = userContextService.getCurrentUserOrThrow();
        CartDTO cartDTO = cartService.getCart(user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<CartDTO>builder()
                .statusCode(200)
                .message("Cart retrieved successfully")
                .data(cartDTO)
                .success(true)
                .build());
    }
    @GetMapping("/debug")
        public ResponseEntity<String> debugCart() {
        return ResponseEntity.ok("The Cart Controller is ALIVE!");
        }
    /**
     * Add item to cart
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponseDTO<CartDTO>> addItemToCart(
            @Valid @RequestBody AddToCartDTO addToCartDTO) {
        User user = userContextService.getCurrentUserOrThrow();
        CartDTO cartDTO = cartService.addItemToCart(user.getUserId(), addToCartDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<CartDTO>builder()
                        .statusCode(201)
                        .message("Item added to cart successfully")
                        .data(cartDTO)
                        .success(true)
                        .build());
    }
    
    /**
     * Remove item from cart
     */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponseDTO<CartDTO>> removeItemFromCart(
            @PathVariable Integer cartItemId) {
        User user = userContextService.getCurrentUserOrThrow();
        CartDTO cartDTO = cartService.removeItemFromCart(user.getUserId(), cartItemId);
        return ResponseEntity.ok(ApiResponseDTO.<CartDTO>builder()
                .statusCode(200)
                .message("Item removed from cart successfully")
                .data(cartDTO)
                .success(true)
                .build());
    }
    
    /**
     * Update item quantity in cart
     */
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<ApiResponseDTO<CartDTO>> updateItemQuantity(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateQuantityDTO updateQuantityDTO) {
        User user = userContextService.getCurrentUserOrThrow();
        CartDTO cartDTO = cartService.updateItemQuantity(user.getUserId(), cartItemId, updateQuantityDTO);
        return ResponseEntity.ok(ApiResponseDTO.<CartDTO>builder()
                .statusCode(200)
                .message("Cart item quantity updated successfully")
                .data(cartDTO)
                .success(true)
                .build());
    }
    
    /**
     * Clear entire cart
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponseDTO<String>> clearCart() {
        User user = userContextService.getCurrentUserOrThrow();
        String result = cartService.clearCart(user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .statusCode(200)
                .message(result)
                .success(true)
                .build());
    }
}
