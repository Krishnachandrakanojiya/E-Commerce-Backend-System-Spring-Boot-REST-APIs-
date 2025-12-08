package com.EcommerceApiApplication.EcommerceApiApplication.DTO;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long id;
    private String paymentId;
    private String status;
    private LocalDateTime paymentDate;
}

