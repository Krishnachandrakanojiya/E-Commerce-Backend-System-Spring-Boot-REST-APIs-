package com.EcommerceApiApplication.EcommerceApiApplication.repository;

import com.EcommerceApiApplication.EcommerceApiApplication.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
