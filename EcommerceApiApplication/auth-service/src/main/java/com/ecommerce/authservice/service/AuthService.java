package com.ecommerce.authservice.service;

import com.ecommerce.common.dto.UserDto;
import com.ecommerce.common.util.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String generateToken(String username, String password) {
        // 1. Call User Service to get user details
        UserDto userDto = webClientBuilder.build()
                .get()
                .uri("http://user-service/api/users/email/" + username)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block(); // Blocking for simplicity in this synchronous flow

        if (userDto == null) {
            throw new RuntimeException("User not found");
        }

        // 2. Validate Password
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Generate Token with roles
        return jwtService.generateToken(username, userDto.getRoles());
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
    
    public UserDto registerUser(UserDto userDto) {
        // The user-service is responsible for all business logic,
        // including encoding passwords and assigning roles.
        // This service should just forward the request.
        return webClientBuilder.build()
                .post()
                .uri("http://user-service/api/users")
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}
