package com.ecommerce.paymentservice.client;

import com.ecommerce.common.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @PutMapping("/api/orders/{id}/status")
    OrderDto updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
}
