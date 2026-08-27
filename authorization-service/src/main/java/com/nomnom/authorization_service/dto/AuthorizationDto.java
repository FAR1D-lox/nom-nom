package com.nomnom.authorization_service.dto;

import com.nomnom.UserRole;

public record AuthorizationDto (
        Long id,
        String username,
        UserRole role
) {
}
