package com.nomnom.authorization_service.service;

import com.nomnom.authorization_service.entity.AuthorizationEntity;
import com.nomnom.authorization_service.dto.LoginRequest;
import com.nomnom.authorization_service.dto.LoginResponse;
import com.nomnom.authorization_service.dto.RegisterRequest;

public interface AuthorizationService {

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    AuthorizationEntity getUserById(Long userId);
}
