package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Builder;
import lombok.Data;

@Builder
public class CartItemDto {
    private Long id;
    private ProductDto product;
    private int quantity;

    public Long getId() {
        return id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CartItemDto() {
    }

    public CartItemDto(Long id, ProductDto product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }
}

