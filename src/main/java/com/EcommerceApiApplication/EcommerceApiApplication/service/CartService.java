package com.EcommerceApiApplication.EcommerceApiApplication.service;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.CartDto;

import java.util.List;

public interface CartService {
    CartDto addItemToCart(Long userId, Long productId, int quantity);
    CartDto updateItemQuantity(Long userId, Long productId, int quantity);
    CartDto removeItemFromCart(Long userId, Long productId);
    CartDto getCartByUserId(Long userId);

}

