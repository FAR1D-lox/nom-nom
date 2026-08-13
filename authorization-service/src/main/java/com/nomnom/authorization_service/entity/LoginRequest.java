package com.nomnom.authorization_service.entity;

public record LoginRequest(
        String username,
        String password
) {
}
