package com.nomnom.ingredient_service.service.impl;

import com.nomnom.ingredient_service.entity.IngredientEntity;
import com.nomnom.ingredient_service.repository.IngredientRepository;
import com.nomnom.ingredient_service.mapper.IngredientMapper;
import com.nomnom.ingredient_service.mapper.RequestAddIngredientDto;
import com.nomnom.ingredient_service.mapper.RequestEditIngredientDto;
import com.nomnom.ingredient_service.mapper.ResponseIngredientDto;
import com.nomnom.ingredient_service.service.IngredientService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository repository;
    private final IngredientMapper mapper;

    @Override
    public ResponseIngredientDto getIngredient(Long ingredientId) {
        IngredientEntity ingredient = repository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient with id = " + ingredientId + " not found"));
        return mapper.toResponseIngredientDto(ingredient);
    }

    @Transactional
    @Override
    public ResponseIngredientDto editIngredient(Long ingredientId, RequestEditIngredientDto editDto) {
        IngredientEntity ingredient = repository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient with id = " + ingredientId + " not found"));

        mapper.updateEntityFromDto(editDto, ingredient);
        return mapper.toResponseIngredientDto(repository.save(ingredient));
    }

    @Transactional
    @Override
    public ResponseIngredientDto addIngredient(RequestAddIngredientDto addDto) {
        if (repository.existsByTitle(addDto.title()))
            throw new EntityExistsException("Ingredient with title " + addDto.title() + " already exists");
        IngredientEntity addedIngredient = IngredientEntity.builder()
                .title(addDto.title())
                .description(addDto.description())
                .previewUrl(addDto.previewUrl())
                .build();
        return mapper.toResponseIngredientDto(repository.save(addedIngredient));
    }

    @Transactional
    @Override
    public boolean removeIngredient(Long ingredientId) {
        return (repository.deleteByIdAndReturnCount(ingredientId) == 1);
    }
}
