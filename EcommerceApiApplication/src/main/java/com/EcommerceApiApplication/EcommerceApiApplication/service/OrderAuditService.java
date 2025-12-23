package com.EcommerceApiApplication.EcommerceApiApplication.service;

import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderAuditEvent;

public interface OrderAuditService {
    void recordEvent(Long orderId, OrderAuditEvent event, String message);
}
