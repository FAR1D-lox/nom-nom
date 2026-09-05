package com.nomnom.user_service.mapper;

import com.nomnom.UserRole;
import com.nomnom.user_service.SubscribeVision;

import java.time.LocalDateTime;

public record ResponseUserProfileDto(
        Long id,
        String username,
        UserRole role,
        LocalDateTime createdAt,
        Long subscribersCount,
        Long subscriptionsCount,
        SubscribeVision subscribeVision
) {
}
