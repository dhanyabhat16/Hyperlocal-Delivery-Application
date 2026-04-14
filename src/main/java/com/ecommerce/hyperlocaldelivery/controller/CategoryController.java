package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.config.AppConstant;
import com.ecommerce.hyperlocaldelivery.payload.CategoryDTO;
import com.ecommerce.hyperlocaldelivery.payload.CategoryResponse;
import com.ecommerce.hyperlocaldelivery.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//categories should be as simple as possible not much validations just make direct call to the service layer which inturn will handle all the error try catch handling


//presence of a controller is doing dependency inject via constructor
//but u could also do a field inject using the @Autowired so you would not need constructor
@RestController //=@Controller + @ResponseBody this tells that ths class handles both http requests and returns data
@RequestMapping("/api") //since /api is common across all the apis
public class CategoryController {


    @Autowired //field injection
    private CategoryService categoryService;

    /*
    constructor injection
    public CategoryController(CategoryService categoryService){
        this.categoryService=categoryService;
    }

     */

    //@RequestMapping(value = "/api/public/categories",method=RequestMethod.GET)->equivalent to the below line
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstant.PAGE_SIZE)Integer pageSize,
            @RequestParam(name="sortBy",defaultValue = AppConstant.SORT_BY)String sortBy,
            @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_ORDER)String sortOrder
    ){
        CategoryResponse allCategories = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO res=categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
        //try{
        CategoryDTO status=categoryService.deleteCategory(categoryId);
            //return ResponseEntity.ok(status);
            //return new ResponseEntity<>(status, HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(status);//errors will be auto handled in the service layer and the custom exception handler if exists
        //}catch (ResponseStatusException e){
          //  return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        //}
    }

    @PutMapping("/admin/category/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategories(@RequestBody CategoryDTO categoryDTO,@PathVariable Long categoryId){
        //try{
        CategoryDTO updatedCategory=categoryService.updateCategory(categoryDTO,categoryId);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
        //}catch (ResponseStatusException e){
            //return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        //}
    }

}
