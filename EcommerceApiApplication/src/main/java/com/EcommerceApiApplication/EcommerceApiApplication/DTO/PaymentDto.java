package com.EcommerceApiApplication.EcommerceApiApplication.DTO;


import lombok.Data;

import java.time.LocalDateTime;


public class PaymentDto {
    private Long id;
    private String paymentId;
    private String status;
    private LocalDateTime paymentDate;

    public Long getId() {
        return id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}

