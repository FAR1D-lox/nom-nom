package com.nomnom.ingredient_service.mapper;


public record RequestAddIngredientDto(
        String title,
        String description,
        String previewUrl
) {
}
