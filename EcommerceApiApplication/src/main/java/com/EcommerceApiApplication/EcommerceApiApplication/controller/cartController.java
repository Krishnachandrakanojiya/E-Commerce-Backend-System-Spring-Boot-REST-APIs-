package com.EcommerceApiApplication.EcommerceApiApplication.controller;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.CartDto;
import com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartServiceImpl cartServiceImpl;


    @PostMapping("/add")
    public CartDto addToCart(@RequestParam Long id, @RequestParam Long productId, @RequestParam int quantity) {
        return cartServiceImpl.addItemToCart(id, productId, quantity);
    }

    @GetMapping("/{id}")
    public CartDto getCart(@PathVariable Long id) {
        return cartServiceImpl.getCart(id);
    }

    // UPDATE QUANTITY
    @PutMapping("/update")
    public CartDto updateQuantity(@RequestParam Long id, @RequestParam Long productId, @RequestParam int quantity) {

        return cartServiceImpl.updateItemQuantity(id, productId, quantity);
    }


    // REMOVE ITEM
    @DeleteMapping("/remove")
    public CartDto removeItem(@RequestParam Long id, @RequestParam Long productId) {

        return cartServiceImpl.removeItemFromCart(id, productId);
    }


}
