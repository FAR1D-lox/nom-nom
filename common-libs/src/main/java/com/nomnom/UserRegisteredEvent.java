package com.nomnom;

public record UserRegisteredEvent(
        Long id,
        String username,
        UserRole role
) {
}
