package com.nomnom.authorization_service.controller;

import com.nomnom.authorization_service.dto.AuthorizationDto;
import com.nomnom.authorization_service.dto.LoginRequest;
import com.nomnom.authorization_service.dto.LoginResponse;
import com.nomnom.authorization_service.dto.RegisterRequest;
import com.nomnom.authorization_service.entity.*;
import com.nomnom.authorization_service.service.AuthorizationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthorizationController {

    private final AuthorizationService service;

    @PostMapping("/register")
    public LoginResponse registration(@RequestBody RegisterRequest request) {
        log.info("Method 'Register' is called");
        return service.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/me")
    public AuthorizationDto getInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        AuthorizationEntity user = service.getUserById(userId);
        return new AuthorizationDto(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }



}
