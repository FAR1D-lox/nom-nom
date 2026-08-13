package com.nomnom.authorization_service.entity;

import com.nomnom.UserRole;

public record AuthorizationDto (
        Long id,
        String username,
        UserRole role
) {
}
