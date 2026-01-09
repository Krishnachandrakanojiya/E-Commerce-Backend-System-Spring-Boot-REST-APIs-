package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.OrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderAuditRepository extends JpaRepository<OrderAudit, Long> {
    List<OrderAudit> findByOrderId(Long orderId);
}
