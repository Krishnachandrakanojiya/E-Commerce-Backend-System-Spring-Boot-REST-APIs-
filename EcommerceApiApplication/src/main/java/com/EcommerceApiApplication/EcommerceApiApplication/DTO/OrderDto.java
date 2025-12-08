package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderItemDto> items;
    private PaymentDto payment;
}

