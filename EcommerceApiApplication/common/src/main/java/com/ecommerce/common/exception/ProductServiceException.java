package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public class ProductServiceException extends BaseException {
    public ProductServiceException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
