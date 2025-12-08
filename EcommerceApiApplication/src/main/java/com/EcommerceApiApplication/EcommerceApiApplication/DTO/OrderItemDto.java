package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private ProductDto product;
    private int quantity;
    private double price;
}

