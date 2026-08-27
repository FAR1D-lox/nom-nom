package com.nomnom.user_service;

import com.nomnom.user_service.dto.UserProfileDto;
import com.nomnom.user_service.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @RequestMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserProfileDto showMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Called 'showMyProfile'");
        Long userId = Long.parseLong(userDetails.getUsername());
        return service.getProfile(userId);
    }

    @RequestMapping("/{userId}")
    public UserProfileDto showOtherProfile(@PathVariable Long userId) {
        log.info("Called 'showOtherProfile'");
        return service.getProfile(userId);
    }

    @RequestMapping("/all")
    public List<UserProfileDto> showAll() {
        log.info("Called 'showAll'");
        return service.getAllProfiles();
    }

}
