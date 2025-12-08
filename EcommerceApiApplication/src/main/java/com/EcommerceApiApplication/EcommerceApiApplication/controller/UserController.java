package com.EcommerceApiApplication.EcommerceApiApplication.controller;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.UserDto;
import com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @RequestMapping(value = "/{id}" , method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        UserDto user = userServiceImpl.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
