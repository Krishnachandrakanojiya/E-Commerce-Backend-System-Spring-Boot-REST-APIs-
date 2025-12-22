package com.EcommerceApiApplication.EcommerceApiApplication.repository;

import com.EcommerceApiApplication.EcommerceApiApplication.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long id);
}

