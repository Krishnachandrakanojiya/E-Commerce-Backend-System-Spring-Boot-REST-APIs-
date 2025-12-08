package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private ProductDto product;
    private int quantity;
}

