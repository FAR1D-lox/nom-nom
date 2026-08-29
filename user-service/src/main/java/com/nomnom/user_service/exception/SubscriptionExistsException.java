package com.nomnom.user_service.exception;

public class SubscriptionExistsException extends RuntimeException {
    public SubscriptionExistsException(String message) {
        super(message);
    }
}
