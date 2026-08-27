package com.nomnom.authorization_service.dto;

public record LoginRequest(
        String username,
        String password
) {
}
