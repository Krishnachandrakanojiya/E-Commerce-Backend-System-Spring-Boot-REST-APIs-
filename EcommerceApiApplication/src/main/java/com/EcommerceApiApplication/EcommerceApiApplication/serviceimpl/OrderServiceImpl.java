package com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderDto;
import com.EcommerceApiApplication.EcommerceApiApplication.DTO.OrderItemDto;
import com.EcommerceApiApplication.EcommerceApiApplication.DTO.ProductDto;
import com.EcommerceApiApplication.EcommerceApiApplication.Enum.OrderStatus;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.*;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.CartRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.OrderRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.repository.UserRepository;
import com.EcommerceApiApplication.EcommerceApiApplication.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<OrderDto> getOrdersByUser(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderDto dto = mapToDto(order);
            orderDtos.add(dto);
        }
        return orderDtos;
    }

    @Override
    public OrderDto placeOrder(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            order.setStatus(OrderStatus.CREATED);
            total += cartItem.getPrice() * cartItem.getQuantity();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        // clear cart after order
        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);

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

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Prevent invalid transitions if needed
        if (order.getStatus() == OrderStatus.DELIVERED && status == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel a delivered order");
        }

        order.setStatus(status);

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }




}
