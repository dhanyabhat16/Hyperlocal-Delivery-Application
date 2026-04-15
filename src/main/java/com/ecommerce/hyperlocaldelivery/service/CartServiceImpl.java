package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.AddToCartDTO;
import com.ecommerce.hyperlocaldelivery.dto.CartDTO;
import com.ecommerce.hyperlocaldelivery.dto.CartItemDTO;
import com.ecommerce.hyperlocaldelivery.dto.UpdateQuantityDTO;
import com.ecommerce.hyperlocaldelivery.entity.Cart;
import com.ecommerce.hyperlocaldelivery.entity.CartItem;
import com.ecommerce.hyperlocaldelivery.entity.Product;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.exception.InsufficientStockException;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.repository.CartItemRepository;
import com.ecommerce.hyperlocaldelivery.repository.CartRepository;
import com.ecommerce.hyperlocaldelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.hyperlocaldelivery.service.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    /**
     * Get or create cart for user
     */
    @Override
    public CartDTO getOrCreateCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Optional<Cart> existingCart = cartRepository.findByUser(user);
        Cart cart;
        
        if (existingCart.isPresent()) {
            cart = existingCart.get();
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        
        return convertToDTO(cart);
    }
    
    /**
     * Add item to cart
     */
    @Override
    public CartDTO addItemToCart(Integer userId, AddToCartDTO addToCartDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Product product = productService.getProductEntityById(addToCartDTO.getProductId());
        
        // Check stock availability
        if (product.getQuantity() < addToCartDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + 
                    ". Available: " + product.getQuantity() + ", Requested: " + addToCartDTO.getQuantity());
        }
        
        // Get or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
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
     * Remove item from cart
     */
    @Override
    public CartDTO removeItemFromCart(Integer userId, Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));
        
        // Verify cart belongs to user
        if (!cartItem.getCart().getUser().getUserId().equals(userId)) {
            throw new InvalidOperationException("Cart item does not belong to this user");
        }
        
        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        
        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return convertToDTO(cart);
    }
    
    /**
     * Update item quantity in cart
     */
    @Override
    public CartDTO updateItemQuantity(Integer userId, Integer cartItemId, UpdateQuantityDTO updateQuantityDTO) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + cartItemId));
        
        // Verify cart belongs to user
        Cart cart = cartItem.getCart();
        if (!cart.getUser().getUserId().equals(userId)) {
            throw new InvalidOperationException("Cart item does not belong to this user");
        }
        
        // Validate quantity
        if (updateQuantityDTO.getQuantity() <= 0) {
            throw new InvalidOperationException("Quantity must be greater than 0");
        }
        
        // Check stock
        if (cartItem.getProduct().getQuantity() < updateQuantityDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + 
                    cartItem.getProduct().getQuantity());
        }
        
        cartItem.setQuantity(updateQuantityDTO.getQuantity());
        cartItemRepository.save(cartItem);
        
        cart = cartRepository.findById(cart.getCartId()).orElseThrow();
        return convertToDTO(cart);
    }
    
    /**
     * Get user's cart
     */
    @Override
    public CartDTO getCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        
        return convertToDTO(cart);
    }
    
    /**
     * Clear cart
     */
    @Override
    public String clearCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        
        cartItemRepository.deleteAll(cart.getItems());
        return "Cart cleared successfully";
    }
    
    /**
     * Helper method to convert Cart entity to DTO
     */
    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> CartItemDTO.builder()
                        .cartItemId(item.getCartItemId())
                        .product(modelMapper.map(item.getProduct(), com.ecommerce.hyperlocaldelivery.dto.ProductDTO.class))
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());
        
        return CartDTO.builder()
                .cartId(cart.getCartId())
                .items(itemDTOs)
                .totalAmount(cart.getTotal())
                .build();
    }
}
