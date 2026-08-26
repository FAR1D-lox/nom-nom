package com.nomnom.user_service.dto;

import com.nomnom.UserRole;

import java.time.LocalDateTime;

public record UserProfileDto(
        String username,
        UserRole role,
        LocalDateTime createdAt
) {
}
