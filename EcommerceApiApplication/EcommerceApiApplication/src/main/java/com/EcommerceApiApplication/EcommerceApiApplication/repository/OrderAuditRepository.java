package com.EcommerceApiApplication.EcommerceApiApplication.repository;

import com.EcommerceApiApplication.EcommerceApiApplication.entity.OrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderAuditRepository extends JpaRepository<OrderAudit, Long> {

    List<OrderAudit> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}
