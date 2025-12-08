package com.EcommerceApiApplication.EcommerceApiApplication.service;


import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderDto;

public interface OrderService {
    OrderDto placeOrder(Long userId);
    OrderDto getOrderById(Long id);
    OrderDto updateOrderStatus(Long id, String status);
}
