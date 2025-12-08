package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private CategoryDto category;
}
