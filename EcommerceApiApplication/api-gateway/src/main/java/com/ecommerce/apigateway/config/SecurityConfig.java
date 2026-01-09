package com.ecommerce.apigateway.config;

import com.ecommerce.common.util.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Permit all requests to the authentication service for registration and login
                        .pathMatchers("/auth-service/api/auth/register", "/auth-service/api/auth/token").permitAll()
                        // All other requests must be authenticated
                        .anyExchange().authenticated()
                );
        return http.build();
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }
}
