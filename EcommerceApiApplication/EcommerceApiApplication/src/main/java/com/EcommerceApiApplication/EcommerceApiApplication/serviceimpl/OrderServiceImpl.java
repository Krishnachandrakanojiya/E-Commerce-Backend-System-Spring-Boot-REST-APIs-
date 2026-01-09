package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderDto;
import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderItemDto;
import com.EcommerceApiApplication.EcommerceApiApplication.DTO.ProductDto;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderAuditEvent;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderStatus;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.PaymentStatus;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.*;
import com.EcommerceApiApplication.EcommerceApiApplication.exception.IllegalOrderStateException;
import com.EcommerceApiApplication.EcommerceApiApplication.exception.OrderNotFoundException;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.CartRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.OrderRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.ProductRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.UserRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.service.OrderService;
import com.EcommerceApiApplication.EcommerceApiApplication.service.PaymentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentServiceImpl paymentServiceImpl;
    private final OrderAuditServiceImpl orderAuditServiceImpl;


    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository, PaymentServiceImpl paymentServiceImpl,OrderAuditServiceImpl orderAuditServiceImpl) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.paymentServiceImpl = paymentServiceImpl;
        this.orderAuditServiceImpl = orderAuditServiceImpl;
    }

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);


    public Order getOrderById(Long orderId) {

        log.info("Fetching order details. orderId={}", orderId);

        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found. orderId={}", orderId);
                    return new RuntimeException("orderId not found");
                });
    }


    @Transactional
    public Order cancelOrder(Long orderId) {

        log.info("Cancel order request received. orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found for cancellation. orderId={}", orderId);
                    return new OrderNotFoundException("Order not found");
                });

        log.debug("Current order status. orderId={}, status={}",
                orderId, order.getStatus());

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {

            log.warn("Invalid cancel attempt. orderId={}, status={}",
                    orderId, order.getStatus());

            throw new IllegalOrderStateException(
                    "Order cannot be cancelled in status: " + order.getStatus());
        }

        // 🔁 INVENTORY ROLLBACK
        for (OrderItem item : order.getItems()) {

            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> {
                        log.error("Product not found during rollback. productId={}",
                                item.getProduct().getId());
                        return new RuntimeException("Product not found");
                    });

            int newStock = product.getStock() + item.getQuantity();
            product.setStock(newStock);
            productRepository.save(product);


            log.info("Inventory restored. productId={}, qtyRestored={}, newStock={}",
                    product.getId(), item.getQuantity(), newStock);
        }

        // 💳 PAYMENT REFUND
        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            log.info("Initiating payment refund. orderId={}", orderId);
            paymentServiceImpl.refundPayment(order.getId());
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            log.info("Payment refunded successfully. orderId={}", orderId);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        // 🧾 ORDER AUDIT — CORRECT PLACE
        orderAuditServiceImpl.recordEvent(
                orderId,
                OrderAuditEvent.ORDER_CANCELLED,
                "Order cancelled by user"
        );

        log.info("Order cancelled successfully. orderId={}", orderId);

        return orderRepository.save(order);
    }


    @Override
    public List<OrderDto> getOrdersByUser(Long userId) {

        log.info("Fetching orders for user. userId={}", userId);

        List<Order> orders = orderRepository.findByUserId(userId);

        log.debug("Total orders found. userId={}, count={}", userId, orders.size());

        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            orderDtos.add(mapToDto(order));
        }
        return orderDtos;
    }


    @Override
    public OrderDto placeOrder(Long id) {

        log.info("Place order request received. userId={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found while placing order. userId={}", id);
                    return new RuntimeException("User not found");
                });

        Cart cart = cartRepository.findByUserId(id)
                .orElseThrow(() -> {
                    log.warn("Cart not found or empty. userId={}", id);
                    return new RuntimeException("Cart is empty");
                });

        if (cart.getItems().isEmpty()) {
            log.warn("Attempt to place order with empty cart. userId={}", id);
            throw new RuntimeException("Cannot place order with empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        double total = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());

            total += cartItem.getPrice() * cartItem.getQuantity();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        log.info("Order placed successfully. orderId={}, totalAmount={}",
                savedOrder.getId(), total);

        orderAuditServiceImpl.recordEvent(
                savedOrder.getId(),
                OrderAuditEvent.ORDER_CREATED,
                "Order created successfully"
        );

        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);

        log.debug("Cart cleared after order placement. userId={}", id);



        return mapToDto(savedOrder);


    }


    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {

        log.info("Update order status request. orderId={}, newStatus={}",
                orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found while updating status. orderId={}", orderId);
                    return new RuntimeException("Order not found");
                });

        if (order.getStatus() == OrderStatus.DELIVERED && status == OrderStatus.CANCELLED) {
            log.warn("Invalid status transition attempted. orderId={}", orderId);
            throw new RuntimeException("Cannot cancel a delivered order");
        }

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);

        log.info("Order status updated successfully. orderId={}, status={}",
                orderId, status);

        return mapToDto(savedOrder);
    }


    private OrderDto mapToDto(Order order) {

        List<OrderItemDto> itemDtos = new ArrayList<>();

        for (OrderItem item : order.getItems()) {

            ProductDto productDto = new ProductDto();
            productDto.setId(item.getProduct().getId());
            productDto.setName(item.getProduct().getName());
            productDto.setPrice(item.getProduct().getPrice());

            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setId(item.getId());
            itemDto.setProduct(productDto);
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());

            itemDtos.add(itemDto);
        }

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setItems(itemDtos);

        return dto;
    }


}
