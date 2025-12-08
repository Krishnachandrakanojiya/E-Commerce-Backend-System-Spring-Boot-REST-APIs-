package com.EcommerceApiApplication.EcommerceApiApplication.service;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.CartDto;

public interface CartService {
    CartDto getCartByUser(Long userId);
    CartDto addItemToCart(Long userId, Long productId, int quantity);
    CartDto removeItemFromCart(Long userId, Long itemId);
    void clearCart(Long userId);
}

