package com.ecommerce.orderservice.service;

import com.ecommerce.common.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(OrderDto orderDto);
    OrderDto getOrderById(Long id);
    List<OrderDto> getOrdersByUserId(Long userId);
    OrderDto updateOrderStatus(Long id, String status);
}
