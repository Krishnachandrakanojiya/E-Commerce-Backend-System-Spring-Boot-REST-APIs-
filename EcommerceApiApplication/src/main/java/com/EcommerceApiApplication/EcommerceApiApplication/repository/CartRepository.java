package com.EcommerceApiApplication.EcommerceApiApplication.repository;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
