package com.ecommerce.productservice.service;

import com.ecommerce.common.dto.ProductDto;
import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(Long id);
    List<ProductDto> getAllProducts();
    ProductDto updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
    ProductDto reduceStock(Long id, int quantity);
    ProductDto addStock(Long id, int quantity);
}
