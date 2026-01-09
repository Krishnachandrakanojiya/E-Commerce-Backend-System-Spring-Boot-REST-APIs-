package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Builder;
import lombok.Data;


@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private CategoryDto category;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public ProductDto() {
    }

    public ProductDto(Long id, String name, String description, double price, int stock, CategoryDto category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}
