package com.ecommerce.cartservice.service.impl;

import com.ecommerce.cartservice.client.ProductServiceClient;
import com.ecommerce.common.dto.CartDto;
import com.ecommerce.common.dto.CartItemDto;
import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.entity.CartItem;
import com.ecommerce.common.exception.CartItemNotFoundException;
import com.ecommerce.common.exception.CartNotFoundException;
import com.ecommerce.common.exception.ProductServiceException;
import com.ecommerce.cartservice.repository.CartItemRepository;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.cartservice.service.CartService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Override
    public CartDto getCartByUserId(Long userId) {
        log.info("Fetching cart for user ID: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Cart not found for user ID: {}, creating new one", userId);
                    return createNewCart(userId);
                });
        return modelMapper.map(cart, CartDto.class);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .build();
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartDto addItemToCart(Long userId, CartItemDto cartItemDto) {
        log.info("Adding item to cart for user ID: {}, Product ID: {}", userId, cartItemDto.getProductId());
        
        ProductDto product;
        try {
            log.debug("Calling Product Service to validate product ID: {}", cartItemDto.getProductId());
            product = productServiceClient.getProductById(cartItemDto.getProductId());
        } catch (FeignException.NotFound ex) {
            log.error("Product not found via Feign client for ID: {}", cartItemDto.getProductId(), ex);
            throw new ProductServiceException("Product not found with ID: " + cartItemDto.getProductId());
        } catch (FeignException ex) {
            log.error("Error calling product service via Feign client", ex);
            throw new ProductServiceException("Error communicating with Product Service: " + ex.getMessage());
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(cartItemDto.getProductId()))
                .findFirst();

        int totalQuantity = cartItemDto.getQuantity();
        if (existingItem.isPresent()) {
            totalQuantity += existingItem.get().getQuantity();
        }

        if (product.getStock() < totalQuantity) {
            log.error("Insufficient stock for product: {}. Requested: {}, Available: {}", product.getName(), totalQuantity, product.getStock());
            throw new ProductServiceException("Insufficient stock for product: " + product.getName());
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(totalQuantity);
            log.info("Updated quantity for existing item in cart");
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(cartItemDto.getProductId())
                    .quantity(cartItemDto.getQuantity())
                    .price(product.getPrice())
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
            log.info("Added new item to cart");
        }

        updateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return modelMapper.map(savedCart, CartDto.class);
    }

    @Override
    @Transactional
    public CartDto removeItemFromCart(Long userId, Long cartItemId) {
        log.info("Removing item ID: {} from cart for user ID: {}", cartItemId, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

        boolean removed = cart.getItems().removeIf(item -> item.getId() != null && item.getId().equals(cartItemId));
        if (!removed) {
            throw new CartItemNotFoundException("Item not found in cart with ID: " + cartItemId);
        }
        
        updateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return modelMapper.map(savedCart, CartDto.class);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user ID: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
        log.info("Cart cleared successfully");
    }

    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }
}
