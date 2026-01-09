package com.ecommerce.orderservice.service.impl;

import com.ecommerce.common.dto.PaymentDto;
import com.ecommerce.common.enums.PaymentStatus;
import com.ecommerce.common.exception.OrderNotFoundException;
import com.ecommerce.common.exception.PaymentNotFoundException;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.Payment;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.repository.PaymentRepository;
import com.ecommerce.orderservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PaymentDto processPayment(PaymentDto paymentDto) {
        log.info("Processing payment for Order ID: {}", paymentDto.getOrderId());
        Order order = orderRepository.findById(paymentDto.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", paymentDto.getOrderId());
                    return new OrderNotFoundException("Order not found");
                });

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentDto.getPaymentMethod())
                .amount(order.getTotalAmount())
                .paymentStatus(PaymentStatus.COMPLETED) // Simulating successful payment
                .transactionId(UUID.randomUUID().toString())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully. Transaction ID: {}", savedPayment.getTransactionId());
        
        // Update order payment status
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        orderRepository.save(order);

        return modelMapper.map(savedPayment, PaymentDto.class);
    }

    @Override
    public PaymentDto getPaymentByOrderId(Long orderId) {
        log.info("Fetching payment details for Order ID: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Payment details not found for order ID: {}", orderId);
                    return new PaymentNotFoundException("Payment details not found for order ID: " + orderId);
                });
        return modelMapper.map(payment, PaymentDto.class);
    }
}
