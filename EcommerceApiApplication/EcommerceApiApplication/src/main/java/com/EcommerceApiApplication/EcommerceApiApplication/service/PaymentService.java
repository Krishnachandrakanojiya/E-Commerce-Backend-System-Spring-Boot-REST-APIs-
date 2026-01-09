package com.EcommerceApiApplication.EcommerceApiApplication.service;


import com.EcommerceApiApplication.EcommerceApiApplication.DTO.PaymentDto;

public interface PaymentService {
    PaymentDto processPayment(Long orderId, PaymentDto paymentDto);
}

