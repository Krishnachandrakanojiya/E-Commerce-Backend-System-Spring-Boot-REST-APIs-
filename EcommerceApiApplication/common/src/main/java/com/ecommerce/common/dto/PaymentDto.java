package com.ecommerce.common.dto;

import com.ecommerce.common.enums.PaymentMethod;
import com.ecommerce.common.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private double amount;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private String transactionId;
}
