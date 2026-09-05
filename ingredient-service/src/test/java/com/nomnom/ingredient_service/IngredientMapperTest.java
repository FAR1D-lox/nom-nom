package com.nomnom.ingredient_service;

import com.nomnom.ingredient_service.entity.IngredientEntity;
import com.nomnom.ingredient_service.mapper.IngredientMapper;
import com.nomnom.ingredient_service.mapper.IngredientMapperImpl;
import com.nomnom.ingredient_service.mapper.RequestEditIngredientDto;
import com.nomnom.ingredient_service.mapper.ResponseIngredientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IngredientMapperTest {

    private IngredientMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IngredientMapperImpl();
    }


    @Test
    void toResponseIngredientDto_ShouldMapAllFields_WhenEntityIsNotNull() {
        LocalDateTime now = LocalDateTime.now();
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .createdAt(now)
                .updatedAt(now)
                .title("Морковь")
                .description("Свежая овощная нарезка")
                .previewUrl("http://image.url/carrot.jpg")
                .build();

        ResponseIngredientDto dto = mapper.toResponseIngredientDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.id());
        assertEquals(entity.getCreatedAt(), dto.createdAt());
        assertEquals(entity.getUpdatedAt(), dto.updatedAt());
        assertEquals(entity.getTitle(), dto.title());
        assertEquals(entity.getDescription(), dto.description());
        assertEquals(entity.getPreviewUrl(), dto.previewUrl());
    }

    @Test
    void toResponseIngredientDto_ShouldReturnNull_WhenEntityIsNull() {
        ResponseIngredientDto dto = mapper.toResponseIngredientDto(null);

        assertNull(dto);
    }

    // ==================== UPDATE ENTITY FROM DTO TESTS ====================

    @Test
    void updateEntityFromDto_ShouldUpdateAllFields_WhenDtoHasAllValues() {
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .title("Старое название")
                .description("Старое описание")
                .previewUrl("http://old.url")
                .build();

        RequestEditIngredientDto dto = new RequestEditIngredientDto(
                "Новое название",
                "Новое описание",
                "http://new.url"
        );

        IngredientEntity result = mapper.updateEntityFromDto(dto, entity);

        assertNotNull(result);
        assertEquals("Новое название", result.getTitle());
        assertEquals("Новое описание", result.getDescription());
        assertEquals("http://new.url", result.getPreviewUrl());
    }

    @Test
    void updateEntityFromDto_ShouldIgnoreNullFields_WhenDtoHasNullValues() {
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .title("Старое название")
                .description("Старое описание")
                .previewUrl("http://old.url")
                .build();

        // Передаем DTO с null-полями для проверки NullValuePropertyMappingStrategy.IGNORE
        RequestEditIngredientDto dto = new RequestEditIngredientDto(
                "Новое название",
                null,
                null
        );

        IngredientEntity result = mapper.updateEntityFromDto(dto, entity);

        assertNotNull(result);
        assertEquals("Новое название", result.getTitle());
        assertEquals("Старое описание", result.getDescription()); // Значение не изменилось
        assertEquals("http://old.url", result.getPreviewUrl());  // Значение не изменилось
    }

    @Test
    void updateEntityFromDto_ShouldUpdateDescriptionAndPreviewUrl_WhenTitleIsNull() {
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .title("Исходное название")
                .description("Исходное описание")
                .previewUrl("http://old.url")
                .build();

        RequestEditIngredientDto dto = new RequestEditIngredientDto(
                null, // title == null (пропускаем обновление title)
                "Новое описание",
                "http://new.url"
        );

        IngredientEntity result = mapper.updateEntityFromDto(dto, entity);

        assertNotNull(result);
        assertEquals("Исходное название", result.getTitle()); // Не изменилось
        assertEquals("Новое описание", result.getDescription());
        assertEquals("http://new.url", result.getPreviewUrl());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateOnlyPreviewUrl_WhenTitleAndDescriptionAreNull() {
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .title("Исходное название")
                .description("Исходное описание")
                .previewUrl("http://old.url")
                .build();

        RequestEditIngredientDto dto = new RequestEditIngredientDto(
                null, // title == null
                null, // description == null
                "http://new.url"
        );

        IngredientEntity result = mapper.updateEntityFromDto(dto, entity);

        assertNotNull(result);
        assertEquals("Исходное название", result.getTitle());       // Не изменилось
        assertEquals("Исходное описание", result.getDescription()); // Не изменилось
        assertEquals("http://new.url", result.getPreviewUrl());
    }

    @Test
    void updateEntityFromDto_ShouldReturnUnchangedEntity_WhenDtoIsNull() {
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .title("Старое название")
                .description("Старое описание")
                .previewUrl("http://old.url")
                .build();

        IngredientEntity result = mapper.updateEntityFromDto(null, entity);

        assertNotNull(result);
        assertEquals("Старое название", result.getTitle());
        assertEquals("Старое описание", result.getDescription());
        assertEquals("http://old.url", result.getPreviewUrl());
    }
}
