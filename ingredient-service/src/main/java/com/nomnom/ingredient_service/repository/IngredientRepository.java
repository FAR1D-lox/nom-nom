package com.nomnom.ingredient_service.repository;

import com.nomnom.ingredient_service.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

    @Modifying
    @NativeQuery("DELETE FROM ingredients WHERE id = :ingredient_id")
    Long deleteByIdAndReturnCount(@Param("ingredient_id") Long ingredientId);

    boolean existsByTitle(String title);
}
