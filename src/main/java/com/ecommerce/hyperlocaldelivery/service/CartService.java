package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.*;
import com.ecommerce.hyperlocaldelivery.entity.*;
import com.ecommerce.hyperlocaldelivery.exception.InsufficientStockException;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.repository.CartItemRepository;
import com.ecommerce.hyperlocaldelivery.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceImpl productService;
    private final CustomerService customerService;
    
    /**
     * Get or create cart for customer
     */
    public CartDTO getOrCreateCart(Integer customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        
        Optional<Cart> existingCart = cartRepository.findByCustomer(customer);
        Cart cart;
        
        if (existingCart.isPresent()) {
            cart = existingCart.get();
        } else {
            cart = new Cart();
            cart.setCustomer(customer);
            cart = cartRepository.save(cart);
        }
        
        return convertToDTO(cart);
    }
    
    /**
     * Add product to cart
     */
    public CartDTO addToCart(Integer customerId, AddToCartDTO addToCartDTO) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        Product product = productService.getProductEntityById(addToCartDTO.getProductId());
        
        // Check stock availability
        if (product.getQuantity() < addToCartDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + 
                    ". Available: " + product.getQuantity() + ", Requested: " + addToCartDTO.getQuantity());
        }
        
        // Get or create cart
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });
        
        // Check if product already exists in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartCartIdAndProductProductId(cart.getCartId(), product.getProductId());
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + addToCartDTO.getQuantity());
            cartItemRepository.save(item);
        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(addToCartDTO.getQuantity());
            newItem.setPrice(product.getPrice());
            cartItemRepository.save(newItem);
        }
        
        // Refresh cart from database
        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return convertToDTO(cart);
    }
    
    /**
     * Update cart item quantity
     */
    public CartDTO updateCartItemQuantity(Integer customerId, UpdateQuantityDTO updateDTO) {
        CartItem cartItem = cartItemRepository.findById(updateDTO.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + updateDTO.getCartItemId()));
        
        // Verify cart belongs to customer
        Cart cart = cartItem.getCart();
        if (!cart.getCustomer().getUserId().equals(customerId)) {
            throw new InvalidOperationException("Cart item does not belong to this customer");
        }
        
        // Validate quantity
        if (updateDTO.getQuantity() <= 0) {
            throw new InvalidOperationException("Quantity must be greater than 0");
        }
        
        // Check stock
        if (cartItem.getProduct().getQuantity() < updateDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + 
                    cartItem.getProduct().getQuantity());
        }
        
        cartItem.setQuantity(updateDTO.getQuantity());
        cartItemRepository.save(cartItem);
        
        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return convertToDTO(cart);
    }
    
    /**
     * Remove item from cart
     */
    public CartDTO removeFromCart(Integer customerId, Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));
        
        // Verify cart belongs to customer
        if (!cartItem.getCart().getCustomer().getUserId().equals(customerId)) {
            throw new InvalidOperationException("Cart item does not belong to this customer");
        }
        
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        
        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return convertToDTO(cart);
    }
    
    /**
     * Clear entire cart
     */
    public void clearCart(Integer customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer: " + customerId));
        
        cartItemRepository.deleteAll(cart.getItems());
    }
    
    /**
     * Helper method to convert Cart entity to DTO
     */
    private CartDTO convertToDTO(Cart cart) {
        return CartDTO.builder()
                .cartId(cart.getCartId())
                .items(cart.getItems() != null ? 
                        cart.getItems().stream().map(this::convertItemToDTO).collect(Collectors.toList()) : 
                        null)
                .totalAmount(cart.getTotal())
                .build();
    }
    
    /**
     * Helper method to convert CartItem to DTO
     */
    private CartItemDTO convertItemToDTO(CartItem item) {
        return CartItemDTO.builder()
                .cartItemId(item.getCartItemId())
                .product(convertProductToDTO(item.getProduct()))
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
    
    /**
     * Helper method to convert Product to DTO
     */
    private ProductDTO convertProductToDTO(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}
