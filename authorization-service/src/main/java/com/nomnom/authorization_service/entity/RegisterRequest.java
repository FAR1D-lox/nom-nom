package com.nomnom.authorization_service.entity;

import com.nomnom.UserRole;

public record RegisterRequest(
        String username,
        String password,
        UserRole role
) {
}
