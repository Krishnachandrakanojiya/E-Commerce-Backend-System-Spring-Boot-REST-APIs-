package com.ecommerce.paymentservice.service.impl;

import com.ecommerce.common.dto.PaymentDto;
import com.ecommerce.common.enums.PaymentStatus;
import com.ecommerce.common.exception.PaymentNotFoundException;
import com.ecommerce.paymentservice.client.OrderServiceClient;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.service.PaymentService;
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
    private ModelMapper modelMapper;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Override
    @Transactional
    public PaymentDto processPayment(PaymentDto paymentDto) {
        log.info("Processing payment for Order ID: {}, Amount: {}", paymentDto.getOrderId(), paymentDto.getAmount());
        
        // In a real scenario, you would call a Payment Gateway here (Stripe/PayPal)
        
        Payment payment = Payment.builder()
                .orderId(paymentDto.getOrderId())
                .paymentMethod(paymentDto.getPaymentMethod())
                .amount(paymentDto.getAmount())
                .paymentStatus(PaymentStatus.COMPLETED) // Simulating successful payment
                .transactionId(UUID.randomUUID().toString())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully. Transaction ID: {}", savedPayment.getTransactionId());
        
        // Update Order Status to PLACED
        try {
            log.info("Updating Order ID: {} status to PLACED", paymentDto.getOrderId());
            orderServiceClient.updateOrderStatus(paymentDto.getOrderId(), "PLACED");
        } catch (Exception e) {
            log.error("Failed to update order status for Order ID: {}", paymentDto.getOrderId(), e);
            // In a real system, you might want to rollback the payment or use a distributed transaction (Saga)
        }
        
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
