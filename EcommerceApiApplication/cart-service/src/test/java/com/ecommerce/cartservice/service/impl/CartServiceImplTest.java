package com.ecommerce.cartservice.service.impl;

import com.ecommerce.cartservice.client.ProductServiceClient;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.entity.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import com.ecommerce.common.dto.CartDto;
import com.ecommerce.common.dto.CartItemDto;
import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.common.exception.CartNotFoundException;
import com.ecommerce.common.exception.ProductServiceException;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartDto cartDto;
    private ProductDto productDto;
    private CartItemDto cartItemDto;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setPrice(100.0);
        productDto.setStock(10);

        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(0.0);

        cartItemDto = new CartItemDto();
        cartItemDto.setProductId(1L);
        cartItemDto.setQuantity(2);

        cartDto = new CartDto();
        cartDto.setId(1L);
        cartDto.setUserId(1L);
    }

    @Test
    void addItemToCart_whenProductIsValid_shouldAddItemSuccessfully() {
        // Arrange
        when(productServiceClient.getProductById(1L)).thenReturn(productDto);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(modelMapper.map(any(Cart.class), any())).thenReturn(cartDto);

        // Act
        CartDto result = cartService.addItemToCart(1L, cartItemDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, cart.getItems().size());
        assertEquals(200.0, cart.getTotalPrice());
        verify(cartRepository).save(cart);
    }

    @Test
    void addItemToCart_whenProductNotFound_shouldThrowProductServiceException() {
        // Arrange
        when(productServiceClient.getProductById(1L)).thenThrow(FeignException.NotFound.class);

        // Act & Assert
        ProductServiceException exception = assertThrows(ProductServiceException.class, () -> {
            cartService.addItemToCart(1L, cartItemDto);
        });

        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addItemToCart_whenInsufficientStock_shouldThrowProductServiceException() {
        // Arrange
        productDto.setStock(1); // Stock is less than quantity in cartItemDto (2)
        when(productServiceClient.getProductById(1L)).thenReturn(productDto);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        ProductServiceException exception = assertThrows(ProductServiceException.class, () -> {
            cartService.addItemToCart(1L, cartItemDto);
        });

        assertEquals("Insufficient stock for product: Test Product", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void removeItemFromCart_whenCartNotFound_shouldThrowCartNotFoundException() {
        // Arrange
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> {
            cartService.removeItemFromCart(99L, 1L);
        });
    }
    
    @Test
    void clearCart_whenCartExists_shouldClearCart() {
        // Arrange
        CartItem item = new CartItem();
        item.setId(1L);
        item.setProductId(1L);
        item.setQuantity(1);
        cart.getItems().add(item);
        cart.setTotalPrice(100.0);
        
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        cartService.clearCart(1L);

        // Assert
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalPrice());
        verify(cartRepository).save(cart);
    }

    @Test
    void clearCart_whenCartNotFound_shouldThrowCartNotFoundException() {
        // Arrange
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> {
            cartService.clearCart(99L);
        });
    }
}
