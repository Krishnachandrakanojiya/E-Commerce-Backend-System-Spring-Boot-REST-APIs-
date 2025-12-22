package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.ProductDto;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Product;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.ProductRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class productServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public productServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    // CREATE
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapToEntity(productDto);
        Product saved = productRepository.save(product);
        return mapToDto(saved);
    }

    // READ by ID
    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return mapToDto(product);
    }

    // READ all
    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> dtos = new ArrayList<>();
        for (Product p : products) {
            dtos.add(mapToDto(p));
        }
        return dtos;
    }

    // UPDATE
    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        existing.setName(productDto.getName());
        existing.setDescription(productDto.getDescription());
        existing.setPrice(productDto.getPrice());
        existing.setStock(productDto.getStock());

        Product updated = productRepository.save(existing);
        return mapToDto(updated);
    }

    // DELETE
    @Override
    public void deleteProduct(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        productRepository.delete(existing);
    }

    // ======================
    // HELPER METHODS
    // ======================
    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        return dto;
    }

    private Product mapToEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return product;
    }
}

