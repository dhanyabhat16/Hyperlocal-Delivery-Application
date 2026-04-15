package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.entity.Product;
import com.ecommerce.hyperlocaldelivery.exception.APIExceptions;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.exception.myResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.entity.Warehouse;
import com.ecommerce.hyperlocaldelivery.model.Category;
import com.ecommerce.hyperlocaldelivery.repository.CategoryRepository;
import com.ecommerce.hyperlocaldelivery.repository.ProductRepository;
import com.ecommerce.hyperlocaldelivery.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    /**
     * Get all products
     */
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get product by ID
     */
    public ProductDTO getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        return convertToDTO(product);
    }
    
    /**
     * Search products by category
     */
    public List<ProductDTO> searchByCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(
                        ()-> new myResourceNotFoundException("Category","Category id",categoryId)
                );
        return productRepository.findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Search products by name
     */
    public List<ProductDTO> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get product entity by ID (internal use)
     */
    public Product getProductEntityById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }
    
    /**
     * Helper method to convert Product entity to DTO
     */
    public ProductDTO convertToDTO(Product product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        if (product.getWarehouse() != null) {
            dto.setWarehouseId(product.getWarehouse().getWarehouseId());
        }
        return dto;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId, Integer warehouseId){
        Product product = modelMapper.map(productDTO, Product.class);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new myResourceNotFoundException("Category","Category id",categoryId)
                );
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new myResourceNotFoundException("Warehouse","Warehouse id",warehouseId));

        boolean noSamePro = true;
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getName().equals(productDTO.getName())) {
                noSamePro = false;
                break;
            }
        }
        if(!noSamePro){
            throw new APIExceptions("Product already exists");
        }
        product.setCategory(category);
        product.setWarehouse(warehouse);
        product.setImage("product_image.png");
        product.setAvailable(true);
        Product addedProd = productRepository.save(product);
        ProductDTO resp = modelMapper.map(addedProd, ProductDTO.class);
        resp.setWarehouseId(warehouseId);
        return resp;
    }

    @Override
    public List<ProductDTO> searchByCategoryAndWarehouse(Long categoryId, Integer warehouseId) {
        return searchByCategory(categoryId)
                .stream()
                .filter(p -> p.getWarehouseId() != null && p.getWarehouseId().equals(warehouseId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchByNameAndWarehouse(String name, Integer warehouseId) {
        return searchByName(name)
                .stream()
                .filter(p -> p.getWarehouseId() != null && p.getWarehouseId().equals(warehouseId))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductByIdAndWarehouse(Integer productId, Integer warehouseId) {
        ProductDTO productDTO = getProductById(productId);
        if (productDTO.getWarehouseId() == null || !productDTO.getWarehouseId().equals(warehouseId)) {
            throw new InvalidOperationException("Product not available in this warehouse");
        }
        return productDTO;
    }

    public ProductDTO updateProduct(Integer productId, ProductDTO productDTO){
        Product existing=productRepository.findById(productId)
                .orElseThrow(()->new myResourceNotFoundException("Product","product id",productId));
        Product product=modelMapper.map(productDTO,Product.class);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setQuantity(product.getQuantity());
        existing.setPrice(product.getPrice());
        return modelMapper.map(productRepository.save(existing),ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductForWarehouse(Integer productId, ProductDTO productDTO, Integer warehouseId) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new myResourceNotFoundException("Product", "product id", productId));
        if (!existing.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new InvalidOperationException("Warehouse can only modify its own products");
        }
        existing.setName(productDTO.getName());
        existing.setDescription(productDTO.getDescription());
        existing.setPrice(productDTO.getPrice());
        existing.setQuantity(productDTO.getQuantity());
        if (productDTO.getAvailable() != null) {
            existing.setAvailable(productDTO.getAvailable());
        }
        return convertToDTO(productRepository.save(existing));
    }

    @Override
    public ProductDTO deleteProduct(Integer productId){
        Product existing=productRepository.findById(productId)
                .orElseThrow(()->new myResourceNotFoundException("Product","product id",productId));

        productRepository.delete(existing);
        return modelMapper.map(existing,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProductForWarehouse(Integer productId, Integer warehouseId) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new myResourceNotFoundException("Product", "product id", productId));
        if (!existing.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new InvalidOperationException("Warehouse can only delete its own products");
        }
        productRepository.delete(existing);
        return convertToDTO(existing);
    }

    @Override
    public ProductDTO updateProductStock(Integer productId, Integer quantity, Integer warehouseId) {
        if (quantity < 0) {
            throw new InvalidOperationException("Stock quantity cannot be negative");
        }
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new myResourceNotFoundException("Product", "product id", productId));
        if (!existing.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new InvalidOperationException("Warehouse can only update stock for its own products");
        }
        existing.setQuantity(quantity);
        return convertToDTO(productRepository.save(existing));
    }

    @Override
    public ProductDTO updateProductAvailability(Integer productId, boolean available, Integer warehouseId) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new myResourceNotFoundException("Product", "product id", productId));
        if (!existing.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new InvalidOperationException("Warehouse can only update availability for its own products");
        }
        existing.setAvailable(available);
        return convertToDTO(productRepository.save(existing));
    }

    @Override
    public ProductDTO updateProductImage(Integer productId, MultipartFile image) throws IOException {
        Product existing=productRepository.findById(productId)
                .orElseThrow(()->new myResourceNotFoundException("Product","product id",productId));

        String path="images/";
        String fileName=uploadImage(path,image);

        existing.setImage(fileName);
        Product product=productRepository.save(existing);
        return modelMapper.map(product,ProductDTO.class);
    }

    private String uploadImage(String path, MultipartFile image) throws IOException {
        String orgName=image.getOriginalFilename();

        String randomId= UUID.randomUUID().toString();
        String fileName=randomId.concat(orgName.substring(orgName.lastIndexOf('.')));//cat.jpg-->random id=123 =>new id= 1234.jpg
        String filePAth=path+ File.separator+fileName;

        File folder=new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        Files.copy(image.getInputStream(), Paths.get(filePAth));

        return fileName;
    }
    
    @Override
    public List<ProductDTO> getProductsByWarehouse(com.ecommerce.hyperlocaldelivery.entity.Warehouse warehouse) {
        return productRepository.findByWarehouse(warehouse)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
