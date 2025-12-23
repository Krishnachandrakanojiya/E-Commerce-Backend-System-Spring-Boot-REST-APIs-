package com.EcommerceApiApplication.EcommerceApiApplication.service;


import com.EcommerceApiApplication.EcommerceApiApplication.DTO.UserDto;

public interface UserService {
    UserDto registerUser(UserDto userDto);

    UserDto getUserById(Long id);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    UserDto loginUser(String email, String password);

    UserDto findByEmail(String email);
}

