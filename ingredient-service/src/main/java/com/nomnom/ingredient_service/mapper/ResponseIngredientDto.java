package com.nomnom.ingredient_service.mapper;

import java.time.LocalDateTime;

public record ResponseIngredientDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String title,
        String description,
        String previewUrl
) {
}
