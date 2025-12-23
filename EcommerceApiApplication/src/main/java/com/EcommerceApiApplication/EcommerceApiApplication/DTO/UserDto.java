package com.EcommerceApiApplication.EcommerceApiApplication.DTO;


import lombok.Data;

import java.util.List;


public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private List<AddressDto> addresses;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }
}
