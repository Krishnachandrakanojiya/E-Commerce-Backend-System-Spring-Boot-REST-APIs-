package com.ecommerce.cartservice.controller;

import com.ecommerce.common.dto.CartDto;
import com.ecommerce.common.dto.CartItemDto;
import com.ecommerce.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable Long userId, @RequestBody CartItemDto cartItemDto) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, cartItemDto));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long userId, @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
