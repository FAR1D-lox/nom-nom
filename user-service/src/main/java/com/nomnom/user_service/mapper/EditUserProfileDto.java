package com.nomnom.user_service.mapper;

import com.nomnom.UserRole;
import com.nomnom.user_service.SubscribeVision;

public record EditUserProfileDto(
        String username,
        UserRole role,
        SubscribeVision subscribeVision
) {
}
