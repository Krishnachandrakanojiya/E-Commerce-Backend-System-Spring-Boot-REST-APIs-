package com.EcommerceApiApplication.EcommerceApiApplication.DTO;


import lombok.Data;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private List<AddressDto> addresses;
}
