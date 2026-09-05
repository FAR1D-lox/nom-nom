package com.nomnom.ingredient_service;

import com.nomnom.ingredient_service.entity.IngredientEntity;
import com.nomnom.ingredient_service.mapper.IngredientMapper;
import com.nomnom.ingredient_service.mapper.RequestAddIngredientDto;
import com.nomnom.ingredient_service.mapper.RequestEditIngredientDto;
import com.nomnom.ingredient_service.mapper.ResponseIngredientDto;
import com.nomnom.ingredient_service.repository.IngredientRepository;
import com.nomnom.ingredient_service.service.impl.IngredientServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {

    @Mock
    private IngredientRepository repository;

    @Mock
    private IngredientMapper mapper;

    @InjectMocks
    private IngredientServiceImpl service;

    private IngredientEntity ingredient;

    private RequestAddIngredientDto addDto;

    private RequestEditIngredientDto editDto;

    private ResponseIngredientDto responseDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        addDto = new RequestAddIngredientDto(
                "eda",
                "da",
                "http://eda"
        );
        editDto = new RequestEditIngredientDto(
                "drugaya eda",
                "net",
                ""
        );
        responseDto = new ResponseIngredientDto(
                1L,
                now,
                now,
                "eda",
                "da",
                "http://eda"
        );
        ingredient = IngredientEntity.builder()
                .id(1L)
                .title("eda")
                .description("da")
                .previewUrl("http://eda")
                .build();
    }

    @Test
    void getIngredient_ShouldReturnResponseDto_WhenIngredientExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(mapper.toResponseIngredientDto(ingredient)).thenReturn(responseDto);

        ResponseIngredientDto result = service.getIngredient(1L);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(repository).findById(1L);
        verify(mapper).toResponseIngredientDto(ingredient);
    }

    @Test
    void getIngredient_ShouldThrowEntityNotFoundException_WhenIngredientNotExists() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getIngredient(99L));
        verify(repository).findById(99L);
        verifyNoInteractions(mapper);
    }




    @Test
    void editIngredient_ShouldReturnResponseDto_WhenIngredientExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(mapper.updateEntityFromDto(editDto, ingredient)).thenReturn(ingredient);
        when(repository.save(ingredient)).thenReturn(ingredient);
        when(mapper.toResponseIngredientDto(ingredient)).thenReturn(responseDto);

        ResponseIngredientDto result = service.editIngredient(1L, editDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(repository).findById(1L);
        verify(mapper).updateEntityFromDto(editDto, ingredient);
        verify(repository).save(ingredient);
        verify(mapper).toResponseIngredientDto(ingredient);
    }

    @Test
    void editIngredient_ShouldThrowEntityNotFoundException_WhenIngredientNotExists() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.editIngredient(99L, editDto));

        verify(repository).findById(99L);
        verify(mapper, never()).updateEntityFromDto(any(), any());
        verify(repository, never()).save(any());
    }




    @Test
    void addIngredient_ShouldReturnResponseDto_WhenIngredientDoesNotExist() {
        when(repository.existsByTitle(addDto.title())).thenReturn(false);
        when(repository.save(any(IngredientEntity.class))).thenReturn(ingredient);
        when(mapper.toResponseIngredientDto(ingredient)).thenReturn(responseDto);

        ResponseIngredientDto result = service.addIngredient(addDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(repository).existsByTitle(addDto.title());
        verify(repository).save(any(IngredientEntity.class));
        verify(mapper).toResponseIngredientDto(ingredient);
    }

    @Test
    void addIngredient_ShouldThrowEntityExistsException_WhenTitleAlreadyExists() {
        when(repository.existsByTitle(addDto.title())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> service.addIngredient(addDto));

        verify(repository).existsByTitle(addDto.title());
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }




    @Test
    void removeIngredient_ShouldReturnTrue_WhenIngredientDeleted() {
        when(repository.deleteByIdAndReturnCount(1L)).thenReturn(1L);

        boolean result = service.removeIngredient(1L);

        assertTrue(result);
        verify(repository).deleteByIdAndReturnCount(1L);
    }

    @Test
    void removeIngredient_ShouldReturnFalse_WhenIngredientNotFound() {
        when(repository.deleteByIdAndReturnCount(99L)).thenReturn(0L);

        boolean result = service.removeIngredient(99L);

        assertFalse(result);
        verify(repository).deleteByIdAndReturnCount(99L);
    }

}
