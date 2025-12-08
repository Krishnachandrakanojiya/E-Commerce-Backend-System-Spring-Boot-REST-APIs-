package com.EcommerceApiApplication.EcommerceApiApplication.service;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
    ProductDto getProductById(Long id);
    List<ProductDto> getAllProducts();
}

