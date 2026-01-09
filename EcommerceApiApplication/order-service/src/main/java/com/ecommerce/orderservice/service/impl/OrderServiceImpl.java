package com.ecommerce.orderservice.service.impl;

import com.ecommerce.common.dto.OrderDto;
import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.enums.PaymentStatus;
import com.ecommerce.common.exception.OrderNotFoundException;
import com.ecommerce.common.exception.ProductServiceException;
import com.ecommerce.orderservice.client.ProductServiceClient;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderAudit;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.repository.OrderAuditRepository;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderAuditRepository orderAuditRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Override
    @Transactional
    public OrderDto placeOrder(OrderDto orderDto) {
        log.info("Placing new order for user ID: {}", orderDto.getUserId());
        Order order = modelMapper.map(orderDto, Order.class);
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new ProductServiceException("Order must contain at least one item.");
        }

        try {
            // Step 1: Verify stock for all products first
            Map<Long, ProductDto> productMap = order.getItems().stream()
                    .collect(Collectors.toMap(OrderItem::getProductId, item -> {
                        log.debug("Fetching product details for Product ID: {}", item.getProductId());
                        return productServiceClient.getProductById(item.getProductId());
                    }));

            for (OrderItem item : order.getItems()) {
                ProductDto product = productMap.get(item.getProductId());
                if (product.getStock() < item.getQuantity()) {
                    throw new ProductServiceException("Insufficient stock for Product ID: " + item.getProductId());
                }
            }

            // Step 2: If all checks pass, calculate total and reduce stock
            double totalAmount = 0.0;
            for (OrderItem item : order.getItems()) {
                ProductDto product = productMap.get(item.getProductId());
                item.setPrice(product.getPrice());
                item.setOrder(order);
                totalAmount += (product.getPrice() * item.getQuantity());
                
                log.debug("Reducing stock for Product ID: {}", item.getProductId());
                productServiceClient.reduceStock(item.getProductId(), item.getQuantity());
            }
            order.setTotalAmount(totalAmount);

        } catch (FeignException.NotFound ex) {
            log.error("Product not found via Feign client", ex);
            throw new ProductServiceException("One or more products in the order were not found.");
        } catch (FeignException ex) {
            log.error("Error calling product service via Feign client", ex);
            throw new ProductServiceException("Error communicating with Product Service: " + ex.getMessage());
        }
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with ID: {}. Total Amount: {}", savedOrder.getId(), savedOrder.getTotalAmount());
        
        logOrderAudit(savedOrder.getId(), OrderStatus.CREATED);
        
        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for user ID: {}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long id, String status) {
        log.info("Updating status for order ID: {} to {}", id, status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
        
        OrderStatus newStatus = OrderStatus.valueOf(status);
        
        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            try {
                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
                        productServiceClient.addStock(item.getProductId(), item.getQuantity());
                    }
                }
            } catch (FeignException ex) {
                log.error("Failed to restore stock for cancelled order ID: {}", id, ex);
                throw new ProductServiceException("Failed to communicate with Product Service to restore stock.");
            }
        }

        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.PLACED) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully");
        
        logOrderAudit(updatedOrder.getId(), newStatus);
        
        return modelMapper.map(updatedOrder, OrderDto.class);
    }
    
    private void logOrderAudit(Long orderId, OrderStatus status) {
        log.debug("Logging audit for order ID: {}, Status: {}", orderId, status);
        OrderAudit audit = OrderAudit.builder()
                .orderId(orderId)
                .status(status)
                .changedBy("SYSTEM")
                .build();
        orderAuditRepository.save(audit);
    }
}
