package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends BaseException {
    public AddressNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
