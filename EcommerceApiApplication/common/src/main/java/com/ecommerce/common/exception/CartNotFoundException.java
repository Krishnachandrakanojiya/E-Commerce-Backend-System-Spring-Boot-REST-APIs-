package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public class CartNotFoundException extends BaseException {
    public CartNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
