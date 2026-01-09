package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.AuthRequest;
import com.ecommerce.common.dto.UserDto;
import com.ecommerce.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService service;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(service.registerUser(userDto));
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(service.generateToken(authRequest.getUsername(), authRequest.getPassword()));
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return ResponseEntity.ok().build();
    }
}
