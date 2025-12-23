package com.EcommerceApiApplication.EcommerceApiApplication.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String msg) {
        super(msg);
    }
}
