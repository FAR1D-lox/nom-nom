package com.nomnom.authorization_service.dto;

import com.nomnom.UserRole;

public record RegisterRequest(
        String username,
        String password,
        UserRole role
) {
}
