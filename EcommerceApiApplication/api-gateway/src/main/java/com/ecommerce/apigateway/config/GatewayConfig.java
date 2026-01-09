package com.ecommerce.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth-service/**")
                        .filters(f -> f.rewritePath("/auth-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://auth-service"))
                .route("user-service", r -> r.path("/user-service/**")
                        .filters(f -> f.rewritePath("/user-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://user-service"))
                .route("product-service", r -> r.path("/product-service/**")
                        .filters(f -> f.rewritePath("/product-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/order-service/**")
                        .filters(f -> f.rewritePath("/order-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://order-service"))
                .route("payment-service", r -> r.path("/payment-service/**")
                        .filters(f -> f.rewritePath("/payment-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://payment-service"))
                .route("cart-service", r -> r.path("/cart-service/**")
                        .filters(f -> f.rewritePath("/cart-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://cart-service"))
                .build();
    }
}
