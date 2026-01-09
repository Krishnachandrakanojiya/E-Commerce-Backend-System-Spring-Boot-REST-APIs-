package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {
    public CategoryNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
