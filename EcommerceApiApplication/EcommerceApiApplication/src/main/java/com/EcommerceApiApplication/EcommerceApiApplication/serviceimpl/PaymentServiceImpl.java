package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.Enum.PaymentStatus;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.RefundStatus;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Payment;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl {

    @Autowired
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    public void refundPayment(Long orderId) {

        log.info("Refund request received for orderId={}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Payment not found for refund. orderId={}", orderId);
                    return new RuntimeException("Payment not found");
                });

        log.debug("Current payment status for orderId={} is {}",
                orderId, payment.getPaymentStatus());

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            log.info("No refund required. orderId={}, paymentStatus={}",
                    orderId, payment.getPaymentStatus());
            return;
        }

        // simulate gateway refund
        log.info("Initiating refund with payment gateway. orderId={}", orderId);
        payment.setRefundStatus(RefundStatus.INITIATED);

        // assume refund succeeds
        payment.setRefundStatus(RefundStatus.COMPLETED);
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        log.info("Refund completed successfully. orderId={}", orderId);
    }
}

