package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.ProductDto;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Product;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.ProductRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);



    @Override
    public ProductDto createProduct(ProductDto productDto) {

        log.info("Create product request received. name={}", productDto.getName());

        Product product = mapToEntity(productDto);
        Product saved = productRepository.save(product);

        log.info("Product created successfully. productId={}, stock={}",
                saved.getId(), saved.getStock());

        return mapToDto(saved);
    }


    @Override
    public ProductDto getProductById(Long id) {

        log.info("Fetching product by id. productId={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found. productId={}", id);
                    return new RuntimeException("Product not found with ID: " + id);
                });

        return mapToDto(product);
    }


    @Override
    public List<ProductDto> getAllProducts() {

        log.info("Fetching all products");

        List<Product> products = productRepository.findAll();

        log.debug("Total products found: {}", products.size());

        List<ProductDto> dtos = new ArrayList<>();
        for (Product p : products) {
            dtos.add(mapToDto(p));
        }
        return dtos;
    }


    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {

        log.info("Update product request received. productId={}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for update. productId={}", id);
                    return new RuntimeException("Product not found with ID: " + id);
                });

        existing.setName(productDto.getName());
        existing.setDescription(productDto.getDescription());
        existing.setPrice(productDto.getPrice());
        existing.setStock(productDto.getStock());

        Product updated = productRepository.save(existing);

        log.info("Product updated successfully. productId={}, newStock={}",
                updated.getId(), updated.getStock());

        return mapToDto(updated);
    }


    @Override
    public void deleteProduct(Long id) {

        log.warn("Delete product request received. productId={}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found for delete. productId={}", id);
                    return new RuntimeException("Product not found with ID: " + id);
                });

        productRepository.delete(existing);

        log.info("Product deleted successfully. productId={}", id);
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

