package com.EcommerceApiApplication.EcommerceApiApplication.controller;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.ProductDto;
import com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl.ProductServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductServiceImpl productServiceImpl;

    public ProductController(ProductServiceImpl productServiceImpl) {
        this.productServiceImpl = productServiceImpl;
    }

    // CREATE Product
    @PostMapping("/addProduct")
    public ProductDto addProduct(@RequestBody ProductDto productDto) {
        return productServiceImpl.createProduct(productDto);
    }

    // GET All Products
    @GetMapping("/getAllProducts")
    public List<ProductDto> getAllProducts() {
        return productServiceImpl.getAllProducts();
    }

    // GET Product by ID
    @GetMapping("/getProductById/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productServiceImpl.getProductById(id);
    }

    // UPDATE Product
    @PutMapping("/update/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return productServiceImpl.updateProduct(id, productDto);
    }

    // DELETE Product
    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productServiceImpl.deleteProduct(id);
        return "Product with ID " + id + " deleted successfully.";
    }
}
