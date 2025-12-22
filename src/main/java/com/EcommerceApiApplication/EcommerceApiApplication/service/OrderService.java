package com.EcommerceApiApplication.EcommerceApiApplication.service;


import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderDto;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDto updateOrderStatus(Long orderId, OrderStatus status);
    List<OrderDto> getOrdersByUser(Long userId);
    OrderDto placeOrder(Long userId);

}
