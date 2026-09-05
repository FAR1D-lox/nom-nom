package com.nomnom.ingredient_service.service;

import com.nomnom.ingredient_service.mapper.RequestAddIngredientDto;
import com.nomnom.ingredient_service.mapper.RequestEditIngredientDto;
import com.nomnom.ingredient_service.mapper.ResponseIngredientDto;

public interface IngredientService {
    ResponseIngredientDto getIngredient(Long ingredientId);

    ResponseIngredientDto editIngredient(Long ingredientId, RequestEditIngredientDto editDto);

    ResponseIngredientDto addIngredient(RequestAddIngredientDto addDto);

    boolean removeIngredient(Long ingredientId);
}
