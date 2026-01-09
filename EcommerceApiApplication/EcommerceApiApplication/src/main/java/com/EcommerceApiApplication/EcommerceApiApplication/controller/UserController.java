package com.EcommerceApiApplication.EcommerceApiApplication.controller;

import com.EcommerceApiApplication.EcommerceApiApplication.DTO.UserDto;
import com.EcommerceApiApplication.EcommerceApiApplication.entity.Address;
import com.EcommerceApiApplication.EcommerceApiApplication.serviceimpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/{id}/addresses")
    public Address addAddress(@PathVariable long id, @RequestBody Address address) {
        return userServiceImpl.addAddress(id, address);
    }

    @GetMapping("{id}/address")
    public List<Address> getAddressByUserId(@PathVariable long id) {
        return userServiceImpl.getAddress(id);
    }

    @PutMapping("/{id}/address")
    public Address updateAddressByUsrId(@PathVariable long id, @RequestBody Address address) {
        return userServiceImpl.updateAddress(id, address);
    }

//    @DeleteMapping("{id}/DeleteAddress")
//    public void deleteAddress(@PathVariable long id){
//        userServiceImpl.deleteAddress(id);
//    }


//---------------below are the user part-----
//    @DeleteMapping("/{id}")
//    public void deletUser(@PathVariable Long id){
//        userServiceImpl.deleteUser(id);
//
//    }

    @PutMapping("/update/{id}")
    public UserDto updateUser(@PathVariable long id, @RequestBody UserDto userDto) {
        return userServiceImpl.updateUser(id, userDto);
    }

    @PostMapping("/addUser")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userServiceImpl.registerUser(userDto);
    }

    @GetMapping("/getUserId/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userServiceImpl.getUserById(id);
        return ResponseEntity.ok(user);
    }


}
