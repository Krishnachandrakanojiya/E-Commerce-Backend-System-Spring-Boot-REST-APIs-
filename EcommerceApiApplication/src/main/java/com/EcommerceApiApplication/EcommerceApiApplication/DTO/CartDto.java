package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Builder;
import lombok.Data;
import java.util.List;


@Builder
public class CartDto {
    private Long id;
    private List<CartItemDto> items;
    private double totalPrice;

    public Long getId() {
        return id;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public CartDto() {
    }

    public CartDto(Long id, List<CartItemDto> items, double totalPrice) {
        this.id = id;
        this.items = items;
        this.totalPrice = totalPrice;
    }
}
