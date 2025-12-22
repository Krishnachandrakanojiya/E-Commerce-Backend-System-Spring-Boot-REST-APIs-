package com.EcommerceApiApplication.EcommerceApiApplication.controller;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderDto;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderStatus;
import com.EcommerceApiApplication.EcommerceApiApplication.service.OrderService;
import com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl.OrderServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class orderController {

    private final OrderServiceImpl orderServiceImpl;


    public orderController(OrderServiceImpl orderServiceImpl) {
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


        @GetMapping("/user")
        public List<OrderDto> getOrdersByUser(@RequestParam Long id) {
            return orderServiceImpl.getOrdersByUser(id);
        }
    }



