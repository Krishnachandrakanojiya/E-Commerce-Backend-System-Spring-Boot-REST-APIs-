package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public class CartItemNotFoundException extends BaseException {
    public CartItemNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
