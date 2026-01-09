package com.ecommerce.common.dto;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private Long userId;
    private String name;
    private String email;
    private String password; // Removed @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<AddressDto> addresses;
    private Set<String> roles;
}
