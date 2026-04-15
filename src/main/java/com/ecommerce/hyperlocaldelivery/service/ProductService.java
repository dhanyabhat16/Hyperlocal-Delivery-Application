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
     * Search products by category and warehouse
     */
    List<ProductDTO> searchByCategoryAndWarehouse(Long categoryId, Integer warehouseId);

    /**
     * Search products by name
     */
    public List<ProductDTO> searchByName(String name);//done

    /**
     * Search products by name and warehouse
     */
    List<ProductDTO> searchByNameAndWarehouse(String name, Integer warehouseId);

    /**
     * Get product by id and warehouse
     */
    ProductDTO getProductByIdAndWarehouse(Integer productId, Integer warehouseId);

    /**
     * Get product entity by ID (internal use)
     */
    public Product getProductEntityById(Integer productId);

    /**
     * Helper method to convert Product entity to DTO
     */
    public ProductDTO convertToDTO(Product product);//done

    ProductDTO addProduct(ProductDTO product, Long categoryId, Integer warehouseId);

    ProductDTO updateProduct(Integer productId, ProductDTO product);//done

    ProductDTO deleteProduct(Integer productId);//done

    ProductDTO updateProductForWarehouse(Integer productId, ProductDTO product, Integer warehouseId);

    ProductDTO deleteProductForWarehouse(Integer productId, Integer warehouseId);

    ProductDTO updateProductStock(Integer productId, Integer quantity, Integer warehouseId);

    ProductDTO updateProductAvailability(Integer productId, boolean available, Integer warehouseId);

    ProductDTO updateProductImage(Integer productId, MultipartFile image) throws IOException;
    
    /**
     * Get products by warehouse
     */
    List<ProductDTO> getProductsByWarehouse(com.ecommerce.hyperlocaldelivery.entity.Warehouse warehouse);
}
