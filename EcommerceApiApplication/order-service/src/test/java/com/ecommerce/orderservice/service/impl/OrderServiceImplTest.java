package com.ecommerce.orderservice.service.impl;

import com.ecommerce.common.dto.OrderDto;
import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.common.exception.OrderNotFoundException;
import com.ecommerce.common.exception.ProductServiceException;
import com.ecommerce.orderservice.client.ProductServiceClient;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.repository.OrderAuditRepository;
import com.ecommerce.orderservice.repository.OrderRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderAuditRepository orderAuditRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderDto orderDto;
    private ProductDto productDto;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setPrice(100.0);
        productDto.setStock(10);

        orderItem = new OrderItem();
        orderItem.setProductId(1L);
        orderItem.setQuantity(1);
        orderItem.setPrice(100.0);

        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setItems(Collections.singletonList(orderItem));
        orderItem.setOrder(order);

        orderDto = new OrderDto();
        orderDto.setUserId(1L);
    }

    @Test
    void placeOrder_whenSuccessful_shouldReturnOrderDto() {
        // Arrange
        when(modelMapper.map(any(OrderDto.class), any())).thenReturn(order);
        when(productServiceClient.getProductById(1L)).thenReturn(productDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), any())).thenReturn(orderDto);

        // Act
        OrderDto result = orderService.placeOrder(orderDto);

        // Assert
        assertNotNull(result);
        verify(productServiceClient).getProductById(1L);
        verify(productServiceClient).reduceStock(1L, 1);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_whenProductNotFound_shouldThrowProductServiceException() {
        // Arrange
        when(modelMapper.map(any(OrderDto.class), any())).thenReturn(order);
        when(productServiceClient.getProductById(1L)).thenThrow(FeignException.NotFound.class);

        // Act & Assert
        ProductServiceException exception = assertThrows(ProductServiceException.class, () -> {
            orderService.placeOrder(orderDto);
        });
        
        assertEquals("One or more products in the order were not found.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_whenInsufficientStock_shouldThrowProductServiceException() {
        // Arrange
        productDto.setStock(0); // Not enough stock
        when(modelMapper.map(any(OrderDto.class), any())).thenReturn(order);
        when(productServiceClient.getProductById(1L)).thenReturn(productDto);

        // Act & Assert
        ProductServiceException exception = assertThrows(ProductServiceException.class, () -> {
            orderService.placeOrder(orderDto);
        });

        assertEquals("Insufficient stock for Product ID: 1", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    void getOrderById_whenOrderExists_shouldReturnOrderDto() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);

        // Act
        OrderDto result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(orderDto.getUserId(), result.getUserId());
    }

    @Test
    void getOrderById_whenOrderDoesNotExist_shouldThrowOrderNotFoundException() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(99L);
        });
    }
}
