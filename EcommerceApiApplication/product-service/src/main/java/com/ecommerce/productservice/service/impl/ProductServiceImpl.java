package com.ecommerce.productservice.service.impl;

import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.common.exception.CategoryNotFoundException;
import com.ecommerce.common.exception.InsufficientStockException;
import com.ecommerce.common.exception.ProductNotFoundException;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Creating new product: {}", productDto.getName());
        Product product = modelMapper.map(productDto, Product.class);
        if (productDto.getCategory() != null && productDto.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDto.getCategory().getId())
                    .orElseThrow(() -> {
                        log.error("Category not found with ID: {}", productDto.getCategory().getId());
                        return new CategoryNotFoundException("Category not found with ID: " + productDto.getCategory().getId());
                    });
            product.setCategory(category);
        }
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public ProductDto getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    @Cacheable(value = "products")
    public List<ProductDto> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });
        
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        
        if (productDto.getCategory() != null && productDto.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDto.getCategory().getId())
                    .orElseThrow(() -> {
                        log.error("Category not found with ID: {}", productDto.getCategory().getId());
                        return new CategoryNotFoundException("Category not found with ID: " + productDto.getCategory().getId());
                    });
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"product", "products"}, key = "#id", allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });
        productRepository.delete(product);
        log.info("Product deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto reduceStock(Long id, int quantity) {
        log.info("Reducing stock for product ID: {} by {}", id, quantity);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });
        
        if (product.getStock() < quantity) {
            log.error("Insufficient stock for product ID: {}. Requested: {}, Available: {}", id, quantity, product.getStock());
            throw new InsufficientStockException("Insufficient stock for product ID: " + id);
        }
        
        product.setStock(product.getStock() - quantity);
        Product updatedProduct = productRepository.save(product);
        log.info("Stock reduced successfully for product ID: {}", id);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    @Transactional
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = "products", allEntries = true)
    public ProductDto addStock(Long id, int quantity) {
        log.info("Adding stock for product ID: {} by {}", id, quantity);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });
        
        product.setStock(product.getStock() + quantity);
        Product updatedProduct = productRepository.save(product);
        log.info("Stock added successfully for product ID: {}", id);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }
}
