package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.entity.Product;
import com.ecommerce.hyperlocaldelivery.exception.APIExceptions;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.exception.myResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.model.Category;
import com.ecommerce.hyperlocaldelivery.repository.CategoryRepository;
import com.ecommerce.hyperlocaldelivery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

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
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId){
        Product product=modelMapper.map(productDTO,Product.class);
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(
                        ()-> new myResourceNotFoundException("Category","Category id",categoryId)
                );

        boolean noSamePro=true;
        List<Product> products=category.getProducts();
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
        product.setImage("product_image.png");
        Product addedProd=productRepository.save(product);
        ProductDTO resp=modelMapper.map(addedProd,ProductDTO.class);
        return resp;
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
    public ProductDTO deleteProduct(Integer productId){
        Product existing=productRepository.findById(productId)
                .orElseThrow(()->new myResourceNotFoundException("Product","product id",productId));

        productRepository.delete(existing);
        return modelMapper.map(existing,ProductDTO.class);
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
}
