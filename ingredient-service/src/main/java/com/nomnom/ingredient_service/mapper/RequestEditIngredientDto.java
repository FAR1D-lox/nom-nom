package com.nomnom.ingredient_service.mapper;


public record RequestEditIngredientDto(
        String title,
        String description,
        String previewUrl
) {
}
