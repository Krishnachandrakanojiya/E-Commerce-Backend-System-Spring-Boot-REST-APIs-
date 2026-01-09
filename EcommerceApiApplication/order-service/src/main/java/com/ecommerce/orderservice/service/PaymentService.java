package com.ecommerce.orderservice.service;


import com.ecommerce.common.dto.PaymentDto;

public interface PaymentService {
    PaymentDto processPayment(PaymentDto paymentDto);
    PaymentDto getPaymentByOrderId(Long orderId);
}
