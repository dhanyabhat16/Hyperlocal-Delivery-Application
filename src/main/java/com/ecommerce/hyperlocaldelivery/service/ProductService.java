package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    public List<ProductDTO> getAllProducts();//done

    public ProductDTO getProductById(Integer productId);//done

    /**
     * Search products by category
     */
    public List<ProductDTO> searchByCategory(Long categoryId);//done

    /**
     * Search products by name
     */
    public List<ProductDTO> searchByName(String name);//done

    /**
     * Get product entity by ID (internal use)
     */
    public Product getProductEntityById(Integer productId);

    /**
     * Helper method to convert Product entity to DTO
     */
    public ProductDTO convertToDTO(Product product);//done

    ProductDTO addProduct(ProductDTO product, Long categoryId);//done

    ProductDTO updateProduct(Integer productId, ProductDTO product);//done

    ProductDTO deleteProduct(Integer productId);//done

    ProductDTO updateProductImage(Integer productId, MultipartFile image) throws IOException;
}
