package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderAuditEvent;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.OrderAudit;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.OrderAuditRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.service.OrderAuditService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class OrderAuditServiceImpl implements OrderAuditService {

//    private  final Logger log =  LoggerFactory.getLogger(OrderAuditServiceImpl.class);
//
    private final OrderAuditRepository orderAuditRepository;

    public OrderAuditServiceImpl(OrderAuditRepository orderAuditRepository) {
        this.orderAuditRepository = orderAuditRepository;
    }

    @Override
    public void recordEvent(Long orderId,
                            OrderAuditEvent event,
                            String message) {

        OrderAudit audit = new OrderAudit();
        audit.setOrderId(orderId);
        audit.setEvent(event);
        audit.setMessage(message);
        audit.setCreatedAt(LocalDateTime.now());

        orderAuditRepository.save(audit);

//        log.info("Order audit recorded. orderId={}, event={}", orderId, event);
    }
}

