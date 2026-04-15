package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.AddToCartDTO;
import com.ecommerce.hyperlocaldelivery.dto.CartDTO;
import com.ecommerce.hyperlocaldelivery.dto.UpdateQuantityDTO;

public interface ICartService {
    
    /**
     * Get or create cart for user
     */
    CartDTO getOrCreateCart(Integer userId);
    
    /**
     * Add item to cart
     */
    CartDTO addItemToCart(Integer userId, AddToCartDTO addToCartDTO);
    
    /**
     * Remove item from cart
     */
    CartDTO removeItemFromCart(Integer userId, Integer cartItemId);
    
    /**
     * Update item quantity in cart
     */
    CartDTO updateItemQuantity(Integer userId, Integer cartItemId, UpdateQuantityDTO updateQuantityDTO);
    
    /**
     * Get user's cart
     */
    CartDTO getCart(Integer userId);
    
    /**
     * Clear cart
     */
    String clearCart(Integer userId);
}
