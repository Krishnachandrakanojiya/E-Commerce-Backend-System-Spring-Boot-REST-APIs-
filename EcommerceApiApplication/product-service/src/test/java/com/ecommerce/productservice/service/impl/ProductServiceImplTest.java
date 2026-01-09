package com.ecommerce.productservice.service.impl;


import com.ecommerce.common.dto.CategoryDto;
import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.common.exception.ProductNotFoundException;
import com.ecommerce.common.exception.CategoryNotFoundException;
import com.ecommerce.common.exception.InsufficientStockException;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;
    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Electronics");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1200.00);
        product.setStock(50);
        product.setCategory(category);

        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Laptop");
        productDto.setPrice(1200.00);
        productDto.setStock(50);
        productDto.setCategory(categoryDto);
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProductDto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(productDto.getId(), result.getId());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldThrowProductNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_withValidCategory_shouldCreateSuccessfully() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(any(ProductDto.class), any())).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), any())).thenReturn(productDto);

        ProductDto result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals(productDto.getName(), result.getName());
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_withInvalidCategory_shouldThrowCategoryNotFoundException() {
        productDto.getCategory().setId(99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.createProduct(productDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void reduceStock_withSufficientStock_shouldUpdateStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        int quantityToReduce = 5;
        productService.reduceStock(1L, quantityToReduce);

        assertEquals(45, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    void reduceStock_withInsufficientStock_shouldThrowInsufficientStockException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        int quantityToReduce = 100; // More than available stock

        assertThrows(InsufficientStockException.class, () -> productService.reduceStock(1L, quantityToReduce));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void reduceStock_withInvalidProduct_shouldThrowProductNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.reduceStock(99L, 5));
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void addStock_shouldUpdateStockSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        int quantityToAdd = 10;
        productService.addStock(1L, quantityToAdd);

        assertEquals(60, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    void addStock_withInvalidProduct_shouldThrowProductNotFoundException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.addStock(99L, 10));
        verify(productRepository, never()).save(any(Product.class));
    }
}
