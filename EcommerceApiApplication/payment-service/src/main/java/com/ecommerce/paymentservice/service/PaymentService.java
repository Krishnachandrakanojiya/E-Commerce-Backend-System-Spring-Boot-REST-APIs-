package com.ecommerce.paymentservice.service;

import com.ecommerce.common.dto.PaymentDto;

public interface PaymentService {
    PaymentDto processPayment(PaymentDto paymentDto);
    PaymentDto getPaymentByOrderId(Long orderId);
}
