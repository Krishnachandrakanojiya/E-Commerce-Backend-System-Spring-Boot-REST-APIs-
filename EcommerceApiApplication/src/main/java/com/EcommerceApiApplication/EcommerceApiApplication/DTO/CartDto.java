package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CartDto {
    private Long id;
    private List<CartItemDto> items;
    private double totalPrice;
}
