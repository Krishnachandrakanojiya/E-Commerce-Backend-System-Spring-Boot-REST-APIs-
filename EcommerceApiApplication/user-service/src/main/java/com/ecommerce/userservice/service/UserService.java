package com.ecommerce.userservice.service;

import com.ecommerce.common.dto.AddressDto;
import com.ecommerce.common.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserDto createAdminUser(UserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto getUserByEmail(String email);
    UserDto updateUser(Long id, UserDto userDto);
    AddressDto addAddress(Long userId, AddressDto addressDto);
    List<AddressDto> getAddressesByUserId(Long userId);
    AddressDto updateAddress(Long addressId, AddressDto addressDto);
    void deleteAddress(Long addressId);
}
