package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.payload.CategoryDTO;
import com.ecommerce.hyperlocaldelivery.payload.CategoryResponse;

public interface CategoryService {
    //interface to promote loose coupling
    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);
    CategoryDTO createCategory(CategoryDTO category);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO category, Long categoryId);
}
