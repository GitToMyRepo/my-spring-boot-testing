package com.mywork.rest.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String s) {
        super(s);
    }

    public OrderNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
