package com.nomnom.user_service;

import com.nomnom.user_service.dto.UserProfileDto;
import com.nomnom.user_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @RequestMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserProfileDto showMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return service.getProfile(userId);
    }

    @RequestMapping("/{userId}")
    public UserProfileDto showOtherProfile(@PathVariable Long userId) {
        return service.getProfile(userId);
    }

}
