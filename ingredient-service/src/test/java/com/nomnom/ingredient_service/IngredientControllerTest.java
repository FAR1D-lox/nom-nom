package com.nomnom.ingredient_service;

import com.nomnom.ingredient_service.mapper.RequestAddIngredientDto;
import com.nomnom.ingredient_service.mapper.RequestEditIngredientDto;
import com.nomnom.ingredient_service.mapper.ResponseIngredientDto;
import com.nomnom.ingredient_service.security.GatewayAuthFilter;
import com.nomnom.ingredient_service.security.SecurityConfig;
import com.nomnom.ingredient_service.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SecurityConfig.class, GatewayAuthFilter.class})
public class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IngredientService ingredientService;

    private ResponseIngredientDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new ResponseIngredientDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "ing1",
                "",
                "https://"
        );
    }

    @Test
    void showIngredient_ShouldReturnIngredient() throws Exception {
        when(ingredientService.getIngredient(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/ingredients/show/1"))
                .andExpect(status().isOk());
        verify(ingredientService).getIngredient(1L);
    }

    @Test
    void editIngredient_ShouldReturnIngredient_WhenAdmin() throws Exception {
        RequestEditIngredientDto editDto = new RequestEditIngredientDto("ha", "hahaha", "https://");
        when(ingredientService.editIngredient(1L, editDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/ingredients/edit/1")
                .header("X-User-Id", "1")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isOk());
        verify(ingredientService).editIngredient(1L, editDto);
    }

    @Test
    void editIngredient_ShouldReturnException_WhenNotAdmin() throws Exception {
        RequestEditIngredientDto editDto = new RequestEditIngredientDto("ha", "hahaha", "https://");

        mockMvc.perform(patch("/ingredients/edit/1")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "DEFAULT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void addIngredient_ShouldReturnIngredient_WhenAdmin() throws Exception {
        RequestAddIngredientDto addDto = new RequestAddIngredientDto("ha", "hahaha", "http://");
        when(ingredientService.addIngredient(addDto)).thenReturn(responseDto);

        mockMvc.perform(post("/ingredients/add")
                .header("X-User-Id", "1")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addDto)))
                .andExpect(status().isOk());
        verify(ingredientService).addIngredient(addDto);
    }

    @Test
    void addIngredient_ShouldReturnException_WhenNotAdmin() throws Exception {
        RequestAddIngredientDto addDto = new RequestAddIngredientDto("ha", "hahaha", "http://");

        mockMvc.perform(post("/ingredients/add")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "DEFAULT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteIngredient_ShouldReturnTrue_WhenAdmin() throws Exception {
        when(ingredientService.removeIngredient(1L)).thenReturn(true);

        mockMvc.perform(delete("/ingredients/remove/1")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk());
        verify(ingredientService).removeIngredient(1L);
    }

    @Test
    void deleteIngredient_ShouldReturnException_WhenNotAdmin() throws Exception {
        mockMvc.perform(delete("/ingredients/remove/1")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "DEFAULT"))
                .andExpect(status().isForbidden());
    }

}
