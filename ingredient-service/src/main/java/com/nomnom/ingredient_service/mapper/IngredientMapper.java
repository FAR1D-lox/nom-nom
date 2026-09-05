package com.nomnom.ingredient_service.mapper;

import com.nomnom.ingredient_service.entity.IngredientEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    ResponseIngredientDto toResponseIngredientDto(IngredientEntity ingredientEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    IngredientEntity updateEntityFromDto(RequestEditIngredientDto dto, @MappingTarget IngredientEntity entity);
}
