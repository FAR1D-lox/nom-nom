package com.nomnom.user_service;

import com.nomnom.UserRole;
import com.nomnom.user_service.mapper.EditUserProfileDto;
import com.nomnom.user_service.mapper.UserProfileDto;
import com.nomnom.user_service.security.GatewayAuthFilter;
import com.nomnom.user_service.security.SecurityConfig;
import com.nomnom.user_service.service.UserService;
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
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SecurityConfig.class, GatewayAuthFilter.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserProfileDto profileDto;

    @BeforeEach
    void setUp() {
        profileDto = new UserProfileDto(
                1L,
                "Adolf1",
                UserRole.DEFAULT,
                LocalDateTime.now(),
                0L,
                0L,
                SubscribeVision.VISIBLE
        );
    }

    @Test
    void showMyProfile_ShouldReturnProfile_WhenAuthenticated() throws Exception {
        when(userService.getProfile(1L)).thenReturn(profileDto);

        mockMvc.perform(get("/users/profile/show/me")
                .header("X-User-Id", "1")
                .header("X-User-Role", "DEFAULT"))
                .andExpect(status().isOk());
        verify(userService).getProfile(1L);
    }

    @Test
    void showMyProfile_ShouldReturnProfile_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/users/profile/show/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void showOtherProfile_ShouldReturnProfile() throws Exception {
        when(userService.getProfile(1L)).thenReturn(profileDto);

        mockMvc.perform(get("/users/profile/show/1"))
                .andExpect(status().isOk());
        verify(userService).getProfile(1L);
    }

    @Test
    void showAllShouldReturnList() throws Exception {
        when(userService.getAllProfiles()).thenReturn(List.of(profileDto));

        mockMvc.perform(get("/users/profile/show/all"))
                .andExpect(status().isOk());
        verify(userService).getAllProfiles();
    }

    @Test
    void editMyProfile_ShouldReturnProfile_WhenAuthenticated() throws Exception {
        EditUserProfileDto editDto = new EditUserProfileDto("Adolf", UserRole.DEFAULT, SubscribeVision.INVISIBLE);
        when(userService.editProfile(1L, editDto, false)).thenReturn(profileDto);

        mockMvc.perform(post("/users/profile/edit/me")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "DEFAULT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isOk());
        verify(userService).editProfile(1L, editDto, false);
    }

    //

    @Test
    void editOtherProfile_ShouldReturnProfile_WhenAdmin() throws Exception {
        EditUserProfileDto editDto = new EditUserProfileDto("Adolf", UserRole.ADMIN, SubscribeVision.INVISIBLE);
        when(userService.editProfile(1L, editDto, true)).thenReturn(profileDto);

        mockMvc.perform(post("/users/profile/edit/1")
                .header("X-User-Id", "2")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isOk());

        verify(userService).editProfile(1L, editDto, true);
    }

    @Test
    void editOtherProfile_ShouldReturn403_WhenNotAdmin() throws Exception {
        EditUserProfileDto editDto = new EditUserProfileDto("Adolf", UserRole.ADMIN, SubscribeVision.INVISIBLE);

        mockMvc.perform(post("/users/profile/edit/1")
                        .header("X-User-Id", "2")
                        .header("X-User-Role", "DEFAULT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void showMySubscribers_ShouldReturnSubscribers_WhenAuthenticated() throws Exception{
        when(userService.getSubscribers(1L, 10, 0)).thenReturn(List.of(profileDto));

        mockMvc.perform(get("/users/subscribers/show/me")
                .header("X-User-Id", "1")
                .header("X-User-Role", "DEFAULT")
                .param("pageSize", "10")
                .param("pageNumber", "0"))
                .andExpect(status().isOk());
        verify(userService).getSubscribers(1L, 10, 0);

    }

    @Test
    void showMySubscriptions_ShouldReturnSubscriptions_WhenAuthenticated() throws Exception {
        when(userService.getSubscriptions(1L, 1L, 10, 0)).thenReturn(List.of(profileDto));

        mockMvc.perform(get("/users/subscriptions/show/me")
                .header("X-User-Id", "1")
                .header("X-User-Role", "DEFAULT")
                .param("pageSize", "10")
                .param("pageNumber", "0"))
                .andExpect(status().isOk());
        verify(userService).getSubscriptions(1L, 1L, 10, 0);
    }

    @Test
    void showOtherSubscriptions_ShouldReturnSubscriptions_WhenAuthenticated() throws Exception {
        when(userService.getSubscriptions(1L, 2L, 10, 0)).thenReturn(List.of(profileDto));

        mockMvc.perform(get("/users/subscriptions/show/2")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "DEFAULT")
                        .param("pageSize", "10")
                        .param("pageNumber", "0"))
                .andExpect(status().isOk());

        verify(userService).getSubscriptions(1L, 2L, 10, 0);
    }

    @Test
    void addSubscription_ShouldReturnOk_WhenAuthenticated() throws Exception {
        mockMvc.perform(post("/users/subscriptions/add/2")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "DEFAULT"))
                .andExpect(status().isOk());

        verify(userService).addSubscription(1L, 2L);
    }

    @Test
    void removeSubscription_ShouldReturnOk_WhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/users/subscriptions/remove/2")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "DEFAULT"))
                .andExpect(status().isOk());

        verify(userService).removeSubscription(1L, 2L);
    }


}
