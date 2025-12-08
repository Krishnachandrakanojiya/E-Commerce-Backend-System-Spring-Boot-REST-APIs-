package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
