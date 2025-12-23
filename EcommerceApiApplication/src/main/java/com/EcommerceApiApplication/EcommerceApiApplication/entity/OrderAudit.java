package com.EcommerceApiApplication.EcommerceApiApplication.entity;

import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderAuditEvent;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_audit")
public class OrderAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    private OrderAuditEvent event;

    private String message;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderAuditEvent getEvent() {
        return event;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setEvent(OrderAuditEvent event) {
        this.event = event;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderAudit() {
    }

    public OrderAudit(Long id, Long orderId, OrderAuditEvent event, String message, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.event = event;
        this.message = message;
        this.createdAt = createdAt;
    }
}

