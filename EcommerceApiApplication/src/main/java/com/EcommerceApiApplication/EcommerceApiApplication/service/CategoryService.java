package com.EcommerceApiApplication.EcommerceApiApplication.service;


import com.EcommerceApiApplication.EcommerceApiApplication.DTO.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    void deleteCategory(Long id);

    CategoryDto getCategoryById(Long id);

    List<CategoryDto> getAllCategories();
}

