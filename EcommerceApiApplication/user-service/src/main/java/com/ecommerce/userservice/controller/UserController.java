package com.ecommerce.userservice.controller;

import com.ecommerce.common.dto.AddressDto;
import com.ecommerce.common.dto.UserDto;
import com.ecommerce.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Public endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
    }

    // Endpoint to create an admin user, restricted to ADMINs
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createAdminUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createAdminUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == principal")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == principal")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    // Address endpoints
    @PostMapping("/{userId}/addresses")
    @PreAuthorize("#userId == principal")
    public ResponseEntity<AddressDto> addAddress(@PathVariable Long userId, @RequestBody AddressDto addressDto) {
        return new ResponseEntity<>(userService.addAddress(userId, addressDto), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/addresses")
    @PreAuthorize("#userId == principal")
    public ResponseEntity<List<AddressDto>> getAddressesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getAddressesByUserId(userId));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @RequestBody AddressDto addressDto) {
        // This needs more complex logic to verify ownership, so leaving it for now
        return ResponseEntity.ok(userService.updateAddress(addressId, addressDto));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        // This needs more complex logic to verify ownership, so leaving it for now
        userService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
