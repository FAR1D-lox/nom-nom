package com.nomnom.user_service.controller;

import com.nomnom.user_service.mapper.EditUserProfileDto;
import com.nomnom.user_service.mapper.UserProfileDto;
import com.nomnom.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @RequestMapping("/profile/show/me")
    @PreAuthorize("isAuthenticated()")
    public UserProfileDto showMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Called 'showMyProfile'");
        Long userId = Long.parseLong(userDetails.getUsername());
        return service.getProfile(userId);
    }

    @RequestMapping("/profile/show/{userId}")
    public UserProfileDto showOtherProfile(@PathVariable Long userId) {
        log.info("Called 'showOtherProfile'");
        return service.getProfile(userId);
    }

    //Переделать на пагинацию
    @RequestMapping("/profile/show/all")
    public List<UserProfileDto> showAll() {
        log.info("Called 'showAll'");
        return service.getAllProfiles();
    }

    @PostMapping("/profile/edit/me")
    @PreAuthorize("isAuthenticated()")
    public UserProfileDto editMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EditUserProfileDto edit) {
        log.info("Called 'editMyProfile'");
        Long userId = Long.parseLong(userDetails.getUsername());
        return service.editProfile(userId, edit, false);
    }

    @PostMapping("/profile/edit/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserProfileDto editOtherProfile(
            @PathVariable Long userId,
            @RequestBody EditUserProfileDto edit) {
        log.info("Called 'editOtherProfile");
        return service.editProfile(userId, edit, true);
    }

    @RequestMapping("/subscribers/show/me")
    @PreAuthorize("isAuthenticated()")
    public List<UserProfileDto> showMySubscribers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        log.info("Called 'showMySubscribers'");
        Long userId = Long.parseLong(userDetails.getUsername());
        return service.getSubscribers(userId, pageSize, pageNumber);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/subscriptions/show/me")
    public List<UserProfileDto> showMySubscriptions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        log.info("Called 'showMySubscriptions'");
        Long userId = Long.parseLong(userDetails.getUsername());
        return service.getSubscriptions(userId, userId, pageSize, pageNumber);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/subscriptions/show/{otherId}")
    public List<UserProfileDto> showOtherSubscriptions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long otherId,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber
    ) {
        log.info("Called 'showOtherSubscriptions'");
        Long myId = Long.parseLong(userDetails.getUsername());
        return service.getSubscriptions(myId, otherId, pageSize, pageNumber);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/subscriptions/add/{otherId}")
    public void addSubscription(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("otherId") Long otherId
    ) {
        log.info("Called 'addSubscription'");
        Long myId = Long.parseLong(userDetails.getUsername());
        service.addSubscription(myId, otherId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/subscriptions/remove/{otherId}")
    public void removeSubscription(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("otherId") Long otherId
    ) {
        log.info("Called 'removeSubscription'");
        Long myId = Long.parseLong(userDetails.getUsername());
        service.removeSubscription(myId, otherId);
    }

}