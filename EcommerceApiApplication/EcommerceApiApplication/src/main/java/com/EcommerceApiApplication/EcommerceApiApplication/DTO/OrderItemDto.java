package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Builder;
import lombok.Data;


@Builder
public class OrderItemDto {
    private Long id;
    private ProductDto product;
    private int quantity;
    private double price;

    public Long getId() {
        return id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderItemDto() {
    }

    public OrderItemDto(Long id, ProductDto product, int quantity, double price) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
}

