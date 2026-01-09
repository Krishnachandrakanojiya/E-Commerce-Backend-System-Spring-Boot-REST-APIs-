package com.ecommerce.orderservice.client;

import com.ecommerce.common.dto.ProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    ProductDto getProductById(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}/reduceStock")
    @CircuitBreaker(name = "productService", fallbackMethod = "reduceStockFallback")
    ProductDto reduceStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @PutMapping("/api/products/{id}/addStock")
    @CircuitBreaker(name = "productService", fallbackMethod = "addStockFallback")
    ProductDto addStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    default ProductDto getProductByIdFallback(Long id, Throwable t) {
        // Return a default product or throw a custom exception
        return ProductDto.builder()
                .id(id)
                .name("Unavailable Product")
                .description("Product information is currently unavailable.")
                .price(0.0)
                .stock(0)
                .build();
    }

    default ProductDto reduceStockFallback(Long id, int quantity, Throwable t) {
        // In case of failure, we might want to throw an exception to stop the order process
        throw new RuntimeException("Product Service is currently unavailable. Cannot reduce stock.");
    }

    default ProductDto addStockFallback(Long id, int quantity, Throwable t) {
        // Log the failure and maybe retry later or alert admin
        throw new RuntimeException("Product Service is currently unavailable. Cannot restore stock.");
    }
}
