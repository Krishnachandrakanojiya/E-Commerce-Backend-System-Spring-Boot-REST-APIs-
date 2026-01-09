package com.ecommerce.cartservice.service;

import com.ecommerce.common.dto.CartDto;
import com.ecommerce.common.dto.CartItemDto;

public interface CartService {
    CartDto getCartByUserId(Long userId);
    CartDto addItemToCart(Long userId, CartItemDto cartItemDto);
    CartDto removeItemFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}
