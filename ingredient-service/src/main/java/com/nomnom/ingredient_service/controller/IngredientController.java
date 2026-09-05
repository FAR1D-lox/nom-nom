package com.nomnom.ingredient_service.controller;

import com.nomnom.ingredient_service.mapper.RequestAddIngredientDto;
import com.nomnom.ingredient_service.mapper.RequestEditIngredientDto;
import com.nomnom.ingredient_service.mapper.ResponseIngredientDto;
import com.nomnom.ingredient_service.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
@Slf4j
public class IngredientController {

    private final IngredientService service;

    @RequestMapping("/show/{ingredientId}")
    public ResponseIngredientDto showIngredient(
            @PathVariable Long ingredientId
    ) {
        log.info("Called 'showIngredient");
        return service.getIngredient(ingredientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/edit/{ingredientId}")
    public ResponseIngredientDto editIngredient(
            @PathVariable("ingredientId") Long ingredientId,
            @RequestBody RequestEditIngredientDto editDto
    ) {
        log.info("Called 'editIngredient'");
        return service.editIngredient(ingredientId, editDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseIngredientDto addIngredient(
            @RequestBody RequestAddIngredientDto addDto
    ) {
        log.info("Called 'addIngredient'");
        return service.addIngredient(addDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove/{ingredientId}")
    public boolean removeIngredient(
            @PathVariable("ingredientId") Long ingredientId
    ) {
        log.info("Called 'remove ingredient");
        return service.removeIngredient(ingredientId);
    }
}
