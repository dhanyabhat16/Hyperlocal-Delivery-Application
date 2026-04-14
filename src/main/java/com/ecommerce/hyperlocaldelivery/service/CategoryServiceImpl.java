package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.exception.APIExceptions;
import com.ecommerce.hyperlocaldelivery.exception.myResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.model.Category;
import com.ecommerce.hyperlocaldelivery.payload.CategoryDTO;
import com.ecommerce.hyperlocaldelivery.payload.CategoryResponse;
import com.ecommerce.hyperlocaldelivery.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

//this @Service basically tells spring that create an object ie bean of this class and manage it that is why when the object of type categoryService is
//created we do not use new with categoryServiceImpl the spring handles it for us-> dependency injection

//that is it injects CategoryService categoryService = new CategoryServiceImpl(); in the controller class
@Service
public class CategoryServiceImpl implements CategoryService{

    //private List<Category> categories=new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("ascending")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=categoryRepository.findAll(pageDetails);
        //List<Category>categories=categoryRepository.findAll();
        List<Category>categories=categoryPage.getContent();

        if(categories.isEmpty()) throw new APIExceptions("No categories created till now");

        List<CategoryDTO> categoryDTOS=categories.stream().map(
                category -> modelMapper.map(category,CategoryDTO.class)
        ).toList();

        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getNumberOfElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        //first convert the dto received to a normal category
        Category category=modelMapper.map(categoryDTO,Category.class);
        Category existing=categoryRepository.findByCategoryName(category.getCategoryName());
        if(existing!=null){
            throw new APIExceptions("Category with this name "+category.getCategoryName()+ " already exists");
        }
        Category savedCategory=categoryRepository.save(category);
        CategoryDTO categoryDTO1=modelMapper.map(savedCategory,CategoryDTO.class);
        return categoryDTO1;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category retrivedCategory=categoryRepository.findById(categoryId).orElseThrow(
                ()->new myResourceNotFoundException("Category","Category Id",categoryId)
        );
        categoryRepository.delete(retrivedCategory);
        return modelMapper.map(retrivedCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO catDTO, Long categoryId) {
        Category category=modelMapper.map(catDTO,Category.class);
        Category retrievedCategory=categoryRepository.findById(categoryId).orElseThrow(
                ()->new myResourceNotFoundException("Category","CategoryID",categoryId)
        );
        retrievedCategory.setCategoryName(category.getCategoryName());
        return modelMapper.map(categoryRepository.save(retrievedCategory),CategoryDTO.class);
    }

}
