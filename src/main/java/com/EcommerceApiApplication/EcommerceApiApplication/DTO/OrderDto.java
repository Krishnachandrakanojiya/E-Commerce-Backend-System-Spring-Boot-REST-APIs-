package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


public class OrderDto {
    private Long id;
    private double totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemDto> items;
    private PaymentDto payment;

    public Long getId() {
        return id;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public PaymentDto getPayment() {
        return payment;
    }

    public void setPayment(PaymentDto payment) {
        this.payment = payment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}

