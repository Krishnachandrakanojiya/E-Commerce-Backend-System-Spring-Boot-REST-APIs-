package com.EcommerceApiApplication.EcommerceApiApplication.controller;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderDto;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderStatus;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Order;
import com.EcommerceApiApplication.EcommerceApiApplication.service.OrderService;
import com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;


    public OrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }


    @PostMapping("/place")
    public OrderDto placeOrder(@RequestParam Long id) {
        return orderServiceImpl.placeOrder(id);
    }

    @PutMapping("/update-status")
    public OrderDto updateStatus(@RequestParam Long orderId, @RequestParam OrderStatus status) {
        return orderServiceImpl.updateOrderStatus(orderId, status);
    }

    @GetMapping("/getOrderByOrderID/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderServiceImpl.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderServiceImpl.cancelOrder(orderId));
    }


    @GetMapping("/getOrdersByUser")
    public List<OrderDto> getOrdersByUser(@RequestParam Long id) {
        return orderServiceImpl.getOrdersByUser(id);
    }
}



