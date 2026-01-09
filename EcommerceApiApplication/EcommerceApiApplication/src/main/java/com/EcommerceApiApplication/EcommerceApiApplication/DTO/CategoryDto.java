package com.EcommerceApiApplication.EcommerceApiApplication.DTO;

import lombok.Builder;
import lombok.Data;


@Builder
public class CategoryDto {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
