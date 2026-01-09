package com.EcommerceApiApplication.EcommerceApiApplication.repository;

import com.EcommerceApiApplication.EcommerceApiApplication.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
