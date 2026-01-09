package com.EcommerceApiApplication.EcommerceApiApplication.controller;

import com.EcommerceApiApplication.EcommerceApiApplication.entity.OrderAudit;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.OrderAuditRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderAuditController {

    private final OrderAuditRepository orderAuditRepository;

    public OrderAuditController(OrderAuditRepository orderAuditRepository) {
        this.orderAuditRepository = orderAuditRepository;
    }

    @GetMapping("/{orderId}/timeline")
    public List<OrderAudit> getOrderTimeline(@PathVariable Long orderId) {

        return orderAuditRepository
                .findByOrderIdOrderByCreatedAtAsc(orderId);
    }
}

